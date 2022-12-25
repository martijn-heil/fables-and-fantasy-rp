package com.fablesfantasyrp.plugin.characters.command

import com.fablesfantasyrp.plugin.characters.*
import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.gui.CharacterStatsGui
import com.fablesfantasyrp.plugin.form.YesNoChatPrompt
import com.fablesfantasyrp.plugin.form.promptChat
import com.fablesfantasyrp.plugin.playerinstance.currentPlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.FABLES_ADMIN
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Range
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant

class Commands(private val plugin: SuspendingJavaPlugin) {
	class Characters(private val plugin: SuspendingJavaPlugin,
					 private val playerInstanceRepository: PlayerInstanceRepository) {
		@Command(aliases = ["new"], desc = "Create a new character!")
		@Require(Permission.Command.Characters.New)
		fun new(@Sender sender: Player) {
			plugin.launch {
				val isStaffCharacter: Boolean = if (sender.hasPermission(Permission.Staff)) {
					val prompt = YesNoChatPrompt(sender, miniMessage.deserialize(
							"<prefix> Is this character a staff character?<newline>",
							Placeholder.component("prefix", legacyText(SYSPREFIX))).color(NamedTextColor.GRAY))
					prompt.send()
					prompt.await()
				} else false

				var info: NewCharacterData
				while (true) {
					info = promptNewCharacterInfo(sender)
					sender.sendMessage(info.toString())

					if (characterRepository.allNames().contains(info.name)) {
						sender.sendError("The name '${info.name}' is already in use, please enter a different name.")
						continue
					}

					break
				}

				val playerInstance = playerInstanceRepository.create(PlayerInstance(
						id = 0,
						owner = if (!isStaffCharacter) sender else FABLES_ADMIN,
						description = info.name,
						isActive = true
				))

				val character = characterRepository.create(Character(
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
			characterRepository.forOwner(owner).forEach {
				val dead = if (it.isDead) " ${ChatColor.RED}(dead)" else ""
				val shelved = if (it.isShelved) " ${ChatColor.YELLOW}(shelved)" else ""
				sender.sendMessage("${ChatColor.GRAY}#${it.id} ${it.name}${dead}${shelved}")
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

		@Command(aliases = ["shelf"], desc = "Shelf a character")
		@Require(Permission.Command.Characters.Shelf)
		fun shelf(@Sender sender: Player, @CommandTarget(Permission.Command.Characters.Shelf + ".others") target: Character) {
			val owner = target.playerInstance.owner
			val shelved = characterRepository.forOwner(owner).filter { it.isShelved && !it.isDead }.size
			if (shelved >= 3) {
				sender.sendError("You cannot shelve more than 3 characters!")
				return
			}

			plugin.launch {
				val prompt = YesNoChatPrompt(sender, miniMessage.deserialize(
								"<prefix> Are you sure you want to shelf <green><character_name></green>?<newline>" +
								"You will not be able to unshelf this character for 3 weeks.",
						Placeholder.component("prefix", legacyText(SYSPREFIX)),
						Placeholder.unparsed("character_name", target.name)).color(NamedTextColor.GRAY))
				prompt.send()
				val confirmation: Boolean = prompt.await()
				if (!confirmation) return@launch

				target.isShelved = true
				sender.sendMessage("$SYSPREFIX Shelved ${target.name}")
			}
		}

		@Command(aliases = ["setrace"], desc = "Set a character's race")
		@Require(Permission.Command.Characters.SetRace)
		fun setrace(@Sender sender: CommandSender, race: Race,
					  @CommandTarget(Permission.Command.Characters.SetRace + ".others") target: Character) {
			target.race = race
			sender.sendMessage("$SYSPREFIX Set ${target.name}'s race to $race")
		}

		@Command(aliases = ["changename"], desc = "Change your character's name")
		@Require(Permission.Command.Characters.ChangeName)
		fun changename(@Sender sender: Player,
					   @CommandTarget(Permission.Command.Characters.ChangeName + ".others") target: Character) {
			plugin.launch {
				val oldName = target.name
				val newName = sender.promptChat("$SYSPREFIX Please enter ${oldName}'s new name:")
				target.name = newName
				sender.sendMessage("$SYSPREFIX Changed $oldName's name to $newName")
			}
		}

		@Command(aliases = ["become"], desc = "Become a character")
		@Require(Permission.Command.Characters.Become)
		fun become(@Sender sender: CommandSender, target: Character,
				   @CommandTarget(Permission.Command.Characters.Become + ".others") who: Player) {
			val owner = target.playerInstance.owner

			if (owner == FABLES_ADMIN && !sender.hasPermission(Permission.Staff)) {
				sender.sendError("You do not have permission to become a staff character.")
				return
			} else if (owner != sender && !sender.hasPermission(Permission.Any)) {
				sender.sendError("You do not have permission to become a character that you don't own.")
				return
			}

			who.currentPlayerInstance = target.playerInstance
		}

		@Command(aliases = ["unshelf"], desc = "Unshelf a character")
		@Require(Permission.Command.Characters.Unshelf)
		fun unshelf(@Sender sender: CommandSender,
				  @CommandTarget(Permission.Command.Characters.Unshelf + ".others") target: Character) {
			if (!target.isShelved) {
				sender.sendError("${target.name} is not shelved.")
				return
			}

			val daysLeft = 21 - Duration.between(target.shelvedAt, Instant.now()).toDays()
			if (daysLeft > 0) {
				sender.sendError("You must wait $daysLeft more days before you can unshelf ${target.name}.")
				return
			}

			target.isShelved = false
			sender.sendMessage("$SYSPREFIX Unshelved ${target.name}")
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
