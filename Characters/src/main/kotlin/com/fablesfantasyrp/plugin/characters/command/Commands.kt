package com.fablesfantasyrp.plugin.characters.command

import com.denizenscript.denizen.objects.PlayerTag
import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.characters.*
import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.gui.CharacterStatsGui
import com.fablesfantasyrp.plugin.denizeninterop.denizenRun
import com.fablesfantasyrp.plugin.playerinstance.currentPlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Range
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Instant

class Commands(private val plugin: SuspendingJavaPlugin) {
	@Command(aliases = ["updatestats"], desc = "Update a player's character stats")
	@Require(Permission.Command.Updatestats)
	fun updatestats(@Sender sender: Player, target: CharacterData) {
		val minimums = target.race.boosters + CharacterStats(2U, 2U, 2U, 2U)
		var initialSliderValues = target.stats - minimums
		if (initialSliderValues.strength > 8U ||
				initialSliderValues.defense > 8U ||
				initialSliderValues.agility > 8U ||
				initialSliderValues.intelligence > 8u) {
			sender.sendMessage("$SYSPREFIX Detected that you will be editing legacy player stats, starting with a clean slate.")
			initialSliderValues = CharacterStats(0U, 0U, 0U, 0U)
		}

		val gui = CharacterStatsGui(FablesCharacters.instance, minimums, "#${target.id} ${target.name}'s stats",
				initialSliderValues)

		plugin.launch {
			val result = gui.execute(sender)
			denizenRun("characters_set_new_stats", mapOf(
					Pair("player", PlayerTag(target.player)),
					Pair("id", ElementTag(target.id.toLong())),
					Pair("strength", ElementTag(result.strength.toInt())),
					Pair("defense", ElementTag(result.defense.toInt())),
					Pair("agility", ElementTag(result.agility.toInt())),
					Pair("intelligence", ElementTag(result.intelligence.toInt()))
			))
		}
	}

	class Characters(private val plugin: SuspendingJavaPlugin,
					 private val playerInstanceRepository: PlayerInstanceRepository) {
		@Command(aliases = ["new"], desc = "Create a new character!")
		@Require(Permission.Command.Characters.New)
		fun new(@Sender sender: Player) {
			plugin.launch {
				val info = promptNewCharacterInfo(sender)
				sender.sendMessage(info.toString())

				val playerInstance = playerInstanceRepository.create(PlayerInstance(
						id = 0,
						owner = sender
				))

				val character = playerCharacterRepository.create(Character(
						id = playerInstance.id.toULong(),
						name = info.name,
						age = info.age,
						description = info.description,
						gender = info.gender,
						race = info.race,
						stats = info.stats,
						playerInstance = playerInstance,
						createdAt = Instant.now()))

				sender.currentPlayerInstance = playerInstance
			}
		}

		@Command(aliases = ["list"], desc = "List characters owned by player")
		@Require(Permission.Command.Characters.List)
		fun list(@Sender sender: CommandSender,
				@CommandTarget(Permission.Command.Characters.List + ".others") owner: OfflinePlayer) {
			sender.sendMessage("$SYSPREFIX ${owner.name} has the following characters:")
			playerCharacterRepository.forOwner(owner).forEach {
				val dead = if (it.isDead) " ${ChatColor.RED}(dead)" else ""
				sender.sendMessage("${ChatColor.GRAY}#${it.id} ${it.name}${dead}")
			}
		}

		@Command(aliases = ["card"], desc = "List characters owned by player")
		@Require(Permission.Command.Characters.Card)
		fun card(@Sender sender: CommandSender,
				@CommandTarget(Permission.Command.Characters.Card + ".others") target: Character) {
			sender.sendMessage(characterCard(target))
		}

		@Command(aliases = ["kill"], desc = "Kill a character")
		@Require(Permission.Command.Characters.Kill)
		fun kill(@Sender sender: CommandSender,
				 @CommandTarget(Permission.Command.Characters.Kill + ".others") target: Character) {
			target.isDead = true
			sender.sendMessage("$SYSPREFIX Killed ${target.name}")
		}

		@Command(aliases = ["resurrect"], desc = "Resurrect a character")
		@Require(Permission.Command.Characters.Resurrect)
		fun resurrect(@Sender sender: CommandSender,
				 @CommandTarget(Permission.Command.Characters.Resurrect + ".others") target: Character) {
			target.isDead = false
			sender.sendMessage("$SYSPREFIX Resurrected ${target.name}")
		}

		class Stats(private val plugin: SuspendingJavaPlugin) {
			@Command(aliases = ["set"], desc = "Set character stats")
			@Require(Permission.Command.Characters.Stats.Set)
			fun set(@Sender sender: CommandSender,
					stat: CharacterStatKind,
					@Range(min = 0.0) value: Int,
					@CommandTarget(Permission.Command.Characters.Stats.Set + ".others") target: Character) {
				target.stats = target.stats.with(stat, value.toUInt())
				sender.sendMessage("$SYSPREFIX Set ${target.name}'s ${stat.toString().lowercase()} to $value")
			}

			@Command(aliases = ["edit"], desc = "Edit character stats")
			@Require(Permission.Command.Characters.Stats.Edit)
			fun edit(@Sender sender: Player,
					@CommandTarget(Permission.Command.Characters.Stats.Edit + ".others") target: Character) {
				val minimums = target.race.boosters + CharacterStats(2U, 2U, 2U, 2U)
				var initialSliderValues = target.stats - minimums
				if (initialSliderValues.strength > 8U ||
						initialSliderValues.defense > 8U ||
						initialSliderValues.agility > 8U ||
						initialSliderValues.intelligence > 8u) {
					sender.sendMessage("$SYSPREFIX Detected that you will be editing legacy player stats, starting with a clean slate.")
					initialSliderValues = CharacterStats(0U, 0U, 0U, 0U)
				}

				val gui = CharacterStatsGui(FablesCharacters.instance, minimums, "#${target.id} ${target.name}'s stats",
						initialSliderValues)

				plugin.launch { target.stats = gui.execute(sender) }
			}
		}
	}
}
