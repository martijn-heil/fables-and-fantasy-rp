package com.fablesfantasyrp.plugin.magic.command

import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.characters.data.entity.CharacterRepository
import com.fablesfantasyrp.plugin.magic.*
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.animations.NecromancyBlightAnimation
import com.fablesfantasyrp.plugin.magic.command.provider.OwnAbility
import com.fablesfantasyrp.plugin.magic.command.provider.OwnSpell
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellData
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.magic.exception.OpenTearException
import com.fablesfantasyrp.plugin.magic.gui.SpellbookGui
import com.fablesfantasyrp.plugin.math.*
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.humanReadable
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Range
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Color
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class Commands(private val profileManager: ProfileManager,
			   private val characters: CharacterRepository) {

	@Command(aliases = ["magicdebug", "mdbg"], desc = "Test various things in Magic.")
	@Require("fables.magic.command.magicdebug")
	fun magicDebug(@Sender player: Player, testName: String) {
		when (testName.lowercase()) {
			"drawline" -> {
				val A = player.location + Vector(0, 1, 0)
				val B = A + player.eyeLocation.direction * 64.0 + Vector(0, 1, 0)
				drawLine(Color.FUCHSIA, A, B, 0.25)
			}
			"drawlinecos" -> {
				val A = player.location + Vector(0, 1, 0)
				val B = A + player.eyeLocation.direction * 64.0 + Vector(0, 1, 0)
				drawLineUsingFunction(Color.FUCHSIA, A, B, 0.25, wave())
			}
			"drawlinehelix" -> {
				val A = player.location + Vector(0, 1, 0)
				val B = A + player.eyeLocation.direction * 64.0 + Vector(0, 1, 0)
				drawLineUsingFunction(Color.FUCHSIA, A, B, 0.25, helix())
			}
			"necroblight" -> {
				val animation = NecromancyBlightAnimation()
				FablesMagic.instance.launch {
					animation.execute(player, player.location)
				}
			}
			else -> {
				player.sendMessage("Not a valid test option: $testName")
			}
		}
	}

	@Command(aliases = ["castspell", "cast"], desc = "Cast a magic spell.")
	@Require(Permission.Command.Castspell)
	fun castspell(@Sender sender: Mage, @OwnSpell spell: SimpleSpellData) {
		PLUGIN.launch { sender.tryCastSpell(spell) }
	}

	@Command(aliases = ["opentear"], desc = "Open a tear.")
	@Require(Permission.Command.Opentear)
	fun opentear(@Sender sender: Mage) {
		PLUGIN.launch {
			try {
				sender.openTear()
			} catch (ex: OpenTearException) {
				val player = profileManager.getCurrentForProfile(sender.character.profile)
				player?.sendError(ex.message ?: "Unknown error (${ex.javaClass.simpleName})")
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}
	}

	@Command(aliases = ["closetear"], desc = "Attempt to close a tear.")
	@Require(Permission.Command.Closetear)
	fun closetear(@Sender sender: Mage) {
		val player = profileManager.getCurrentForProfile(sender.character.profile)!!
		PLUGIN.launch {
			val block = player.getTargetBlock(30) ?: run {
				player.sendError("Block too far away")
				return@launch
			}

			val tear = tearRepository.forLocation(block.location) ?: run {
				player.sendError("Targeted block is not a tear")
				return@launch
			}

			sender.tryCloseTear(tear)
		}
	}

	@Command(aliases = ["grimoire", "spellbook"], desc = "Show your grimoire.")
	@Require(Permission.Command.Spellbook)
	fun spellbook(@Sender sender: Player, @CommandTarget(Permission.Command.Spellbook + ".others") mage: Mage) {
		val senderCharacter = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
		SpellbookGui(PLUGIN, mage, readOnly = senderCharacter != mage.character).show(sender)
	}

	@Command(aliases = ["resetspellbook", "resetgrimoire"], desc = "Reset a mage's grimoire.")
	@Require(Permission.Command.Resetspellbook)
	fun resetspellbook(@Sender sender: CommandSender, target: Mage) {
		target.spells = emptyList()
		sender.sendMessage("$SYSPREFIX Reset ${target.character.name}'s grimoire.")
	}

	@Command(aliases = ["tears"], desc = "List all tears.")
	@Require(Permission.Command.Tears)
	fun tears(@Sender sender: CommandSender) {
		val message = Component.text().append(
			tearRepository.all().asSequence().map {
				Component.text()
						.append(Component.text("[${it.location.humanReadable()}]"))
						.append(Component.text(" ${it.magicType} tear by ${it.owner.character.name}"))
						.color(NamedTextColor.GRAY)
		}.join(Component.newline()).toList())

		sender.sendMessage(message)
	}

	@Command(aliases = ["setmagicpath"], desc = "Set a character's magic path.")
	@Require(Permission.Command.Setmagicpath)
	fun setmagicpath(@Sender sender: CommandSender, magicPath: MagicPath, target: CharacterData) {
		val mage = mageRepository.forPlayerCharacterOrCreate(target)
		mage.magicPath = magicPath
		sender.sendMessage("$SYSPREFIX set ${target.name}'s magic path to $magicPath")
	}

	@Command(aliases = ["setmagiclevel"], desc = "Set a character's magic level.")
	@Require(Permission.Command.Setmagiclevel)
	fun setmagiclevel(@Sender sender: CommandSender, target: Mage, @Range(min = 0.00) magicLevel: Int) {
		if (magicLevel != 0) {
			target.magicLevel = magicLevel
			sender.sendMessage("$SYSPREFIX set ${target.character.name}'s magic level to $magicLevel")
		} else {
			mageRepository.destroy(target)
			sender.sendMessage("$SYSPREFIX removed magic from ${target.character.name}")
		}
	}

	inner class Ability {
		@Command(aliases = ["list"], desc = "List your abilities")
		@Require(Permission.Command.Ability.List)
		fun list(@Sender sender: CommandSender, @CommandTarget(Permission.Command.Ability.List + ".others") target: Mage) {
			val allAbilities = MageAbilities.forPath(target.magicPath)
			if (allAbilities == null) {
				sender.sendError("This mage does not have access to any abilities")
				return
			}

			fun activeInactiveComponent(isActive: Boolean): Component {
				return if (isActive) {
					miniMessage.deserialize("<green>[ACTIVE]</green>")
				} else {
					miniMessage.deserialize("<red>[INACTIVE]</red>")
				}
			}

			val abilitiesComponent = Component.text().append(allAbilities
					.asSequence()
					.map { Pair(it, target.activeAbilities.contains(it)) }
					.sortedByDescending { it.second }
					.map {
						val ability = it.first
						val isActive = it.second
						miniMessage.deserialize("    <gray><ability_name>: <active></gray>",
								Placeholder.unparsed("ability_name", ability.displayName),
								Placeholder.component("active", activeInactiveComponent(isActive)))
					}.join(Component.newline()).toList())

			sender.sendMessage(miniMessage.deserialize("<gray><prefix> <mage_name>'s abilities:\n<abilities></gray>",
				Placeholder.component("prefix", legacyText(SYSPREFIX)),
				Placeholder.unparsed("mage_name", target.character.name),
				Placeholder.component("abilities", abilitiesComponent)))
		}

		@Command(aliases = ["activate"], desc = "Activate an ability.")
		@Require(Permission.Command.Ability.Activate)
		fun activate(@Sender sender: Mage,
					 @OwnAbility ability: MageAbility) {
			sender.activeAbilities = sender.activeAbilities.plus(ability)
			profileManager.getCurrentForProfile(sender.character.profile)!!.sendMessage("$SYSPREFIX Activated ${ability.displayName}")
		}

		@Command(aliases = ["deactivate"], desc = "Deactivate an ability.")
		@Require(Permission.Command.Ability.Deactiviate)
		fun deactivate(@Sender sender: Mage,
					 @OwnAbility ability: MageAbility) {
			sender.activeAbilities = sender.activeAbilities.minus(ability)
			profileManager.getCurrentForProfile(sender.character.profile)!!.sendMessage("$SYSPREFIX Deactivated ${ability.displayName}")
		}
	}
}
