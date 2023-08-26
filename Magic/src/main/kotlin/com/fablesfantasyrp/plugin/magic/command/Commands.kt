package com.fablesfantasyrp.plugin.magic.command

import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.magic.*
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.command.provider.OwnAbility
import com.fablesfantasyrp.plugin.magic.command.provider.OwnSpell
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.domain.repository.TearRepository
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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Commands(private val plugin: JavaPlugin,
			   private val profileManager: ProfileManager,
			   private val characters: EntityCharacterRepository,
			   private val tears: TearRepository,
			   private val mages: MageRepository,
			   private val spells: SpellDataRepository) {
	@Command(aliases = ["castspell", "cast"], desc = "Cast a magic spell.")
	@Require(Permission.Command.Castspell)
	fun castspell(@Sender sender: Mage, @OwnSpell spell: SpellData) {
		plugin.launch { sender.tryCastSpell(spell) }
	}

	@Command(aliases = ["opentear"], desc = "Open a tear.")
	@Require(Permission.Command.Opentear)
	fun opentear(@Sender sender: Mage) {
		plugin.launch {
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
		plugin.launch {
			val block = player.getTargetBlock(30) ?: run {
				player.sendError("Block too far away")
				return@launch
			}

			val tear = tears.forLocation(block.location) ?: run {
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
		SpellbookGui(plugin, spells, mage, readOnly = senderCharacter != mage.character).show(sender)
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
			tears.all().asSequence().map {
				Component.text()
						.append(Component.text("[${it.location.humanReadable()}]"))
						.append(Component.text(" ${it.magicType} tear by ${it.owner.character.name}"))
						.color(NamedTextColor.GRAY)
		}.join(Component.newline()).toList())

		sender.sendMessage(message)
	}

	@Command(aliases = ["setmagicpath"], desc = "Set a character's magic path.")
	@Require(Permission.Command.Setmagicpath)
	fun setmagicpath(@Sender sender: CommandSender, magicPath: MagicPath, target: Character) {
		val mage = mages.forCharacterOrCreate(target)
		mage.magicPath = magicPath
		sender.sendMessage("$SYSPREFIX set ${target.name}'s magic path to $magicPath")
	}

	@Command(aliases = ["setmagiclevel"], desc = "Set a character's magic level.")
	@Require(Permission.Command.Setmagiclevel)
	fun setmagiclevel(@Sender sender: CommandSender, target: Mage, @Range(min = 0.00, max = 10.00) magicLevel: Int) {
		if (magicLevel != 0) {
			target.magicLevel = magicLevel
			sender.sendMessage("$SYSPREFIX set ${target.character.name}'s magic level to $magicLevel")
		} else {
			mages.destroy(target)
			sender.sendMessage("$SYSPREFIX removed magic from ${target.character.name}")
		}
	}

	inner class Ability {
		@Command(aliases = ["list"], desc = "List your abilities")
		@Require(Permission.Command.Ability.List)
		fun list(@Sender sender: CommandSender, @CommandTarget(Permission.Command.Ability.List + ".others") target: Mage) {
			val allAbilities = MageAbilities.forPath(target.magicPath)
			if (allAbilities.isEmpty()) {
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
			val player = profileManager.getCurrentForProfile(sender.character.profile)!!
			if (ability.minimumMageLevel > sender.magicLevel) {
				player.sendError("You need at least level ${ability.minimumMageLevel} magic to use this ability.")
				return
			}

			sender.activeAbilities = sender.activeAbilities.plus(ability)
		}

		@Command(aliases = ["deactivate"], desc = "Deactivate an ability.")
		@Require(Permission.Command.Ability.Deactiviate)
		fun deactivate(@Sender sender: Mage,
					   @OwnAbility ability: MageAbility) {
			sender.activeAbilities = sender.activeAbilities.minus(ability)
		}
	}
}
