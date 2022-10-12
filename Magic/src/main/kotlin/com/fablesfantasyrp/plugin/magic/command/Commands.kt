package com.fablesfantasyrp.plugin.magic.command

import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.magic.*
import com.fablesfantasyrp.plugin.magic.animations.NecromancyBlightAnimation
import com.fablesfantasyrp.plugin.magic.command.provider.OwnSpell
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellData
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.magic.exception.OpenTearException
import com.fablesfantasyrp.plugin.magic.gui.SpellbookGui
import com.fablesfantasyrp.plugin.math.*
import com.fablesfantasyrp.plugin.text.join
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.humanReadable
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Optional
import com.sk89q.intake.parametric.annotation.Range
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class Commands {

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
				sender.playerCharacter.player.player?.sendError(ex.message ?: "Unknown error (${ex.javaClass.simpleName})")
			}
		}
	}

	@Command(aliases = ["closetear"], desc = "Attempt to close a tear.")
	@Require(Permission.Command.Closetear)
	fun closetear(@Sender sender: Mage) {
		val player = sender.playerCharacter.player.player!!
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
	fun spellbook(@Sender sender: Mage) {
		val player = sender.playerCharacter.player.player!!
		SpellbookGui(PLUGIN, sender).show(player)
	}

	@Command(aliases = ["resetspellbook", "resetgrimoire"], desc = "Reset a mage's grimoire.")
	@Require(Permission.Command.Resetspellbook)
	fun resetspellbook(@Sender sender: CommandSender, target: Mage) {
		target.spells = emptyList()
		sender.sendMessage("$SYSPREFIX Reset ${target.playerCharacter.name}'s grimoire.")
	}

	@Command(aliases = ["tears"], desc = "List all tears.")
	@Require(Permission.Command.Tears)
	fun tears(@Sender sender: CommandSender) {
		val message = Component.text().append(
			tearRepository.all().asSequence().map {
				Component.text()
						.append(Component.text("[${it.location.humanReadable()}]"))
						.append(Component.text(" ${it.magicType} tear by ${it.owner.playerCharacter.name}"))
						.color(NamedTextColor.GRAY)
		}.join(Component.newline()).toList())

		sender.sendMessage(message)
	}

	@Command(aliases = ["setmagicpath"], desc = "Set a character's magic path.")
	@Require(Permission.Command.Setmagicpath)
	fun setmagicpath(@Sender sender: CommandSender, target: PlayerCharacterData, @Optional magicPath: MagicPath?) {
		if (magicPath != null) {
			val mage = mageRepository.forPlayerCharacterOrCreate(target)
			mage.magicPath = magicPath
			sender.sendMessage("$SYSPREFIX set ${target.name}'s magic path to $magicPath")
		} else {
			mageRepository.forPlayerCharacter(target)?.let { mageRepository.destroy(it) }
			sender.sendMessage("$SYSPREFIX removed magic from ${target.name}")
		}
	}

	@Command(aliases = ["setmagiclevel"], desc = "Set a character's magic level.")
	@Require(Permission.Command.Setmagiclevel)
	fun setmagiclevel(@Sender sender: CommandSender, target: Mage, @Range(min = 0.00) magicLevel: Int) {
		target.magicLevel = magicLevel
		sender.sendMessage("$SYSPREFIX set ${target.playerCharacter.name}'s magic path to $magicLevel")
	}
}
