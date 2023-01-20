package com.fablesfantasyrp.plugin.characters.command

import com.fablesfantasyrp.plugin.characters.*
import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.characters.data.CHARACTER_STATS_FLOOR
import com.fablesfantasyrp.plugin.characters.data.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.gui.CharacterStatsGui
import com.fablesfantasyrp.plugin.form.YesNoChatPrompt
import com.fablesfantasyrp.plugin.form.promptChat
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.ProfileOccupiedException
import com.fablesfantasyrp.plugin.profile.ProfilePrompter
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.timers.CancelReason
import com.fablesfantasyrp.plugin.timers.countdown
import com.fablesfantasyrp.plugin.utils.FABLES_ADMIN
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Optional
import com.sk89q.intake.parametric.annotation.Range
import com.sk89q.intake.parametric.annotation.Switch
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration
import java.time.Instant

class LegacyCommands(private val characterCommands: Commands.Characters) {
	@Command(aliases = ["newcharacter", "newchar", "nc"], desc = "Create a new character!")
	@Require(Permission.Command.Characters.New)
	fun newCharacter(@Sender sender: Player) {
		characterCommands.new(sender)
	}

	@Command(aliases = ["become", "switchcharacter", "switchchar", "characters"], desc = "Become a character")
	@Require(Permission.Command.Characters.Become)
	fun become(@Sender sender: Player,
			   @Optional @AllowCharacterName targetMaybe: Profile?,
			   @Optional @CommandTarget(Permission.Command.Characters.Become + ".others") who: Player,
			   @Switch('f') force: Boolean) {
		characterCommands.become(sender, targetMaybe, who, force)
	}

	@Command(aliases = ["card", "charactercard"], desc = "Display a character's card in chat.")
	@Require(Permission.Command.Characters.Card)
	fun card(@Sender sender: CommandSender, @CommandTarget target: Character) {
		characterCommands.card(sender, target)
	}

	@Command(aliases = ["removecharacter", "removechar", "rc", "permakill", "pk"], desc = "Kill a character")
	@Require(Permission.Command.Characters.Kill)
	fun removeCharacter(@Sender sender: CommandSender, @CommandTarget target: Character) {
		characterCommands.kill(sender, target)
	}
}

class Commands(private val plugin: SuspendingJavaPlugin) {
	class Characters(private val plugin: SuspendingJavaPlugin,
					 private val profileRepository: ProfileRepository,
					 private val profileManager: ProfileManager,
					 private val profilePrompter: ProfilePrompter) {
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

				if (!isStaffCharacter) {
					val usedSlotCount = characterRepository.forOwner(sender).filter { !(it.isDead || it.isShelved) }.size
					val maxSlotCount = sender.characterSlotCount
					if (usedSlotCount >= maxSlotCount) {
						sender.sendError("You already have ${usedSlotCount}/${maxSlotCount} character slots occupied")
						return@launch
					}
				}

				var info: NewCharacterData
				while (true) {
					info = promptNewCharacterInfo(sender,
							Race.values().asSequence()
									.filter { it != Race.HUMAN }
									.filter { if (!isStaffCharacter) it != Race.OTHER else true }
									.toList())

					if (characterRepository.allNames().contains(info.name)) {
						sender.sendError("The name '${info.name}' is already in use, please enter a different name.")
						continue
					}

					break
				}

				val profile = profileRepository.create(Profile(
						owner = if (!isStaffCharacter) sender else FABLES_ADMIN,
						description = info.name,
						isActive = true
				))

				val character = characterRepository.create(Character(
						id = profile.id,
						name = info.name,
						age = info.age,
						description = info.description,
						gender = info.gender,
						race = info.race,
						stats = info.stats,
						profile = profile,
						createdAt = Instant.now()))

				profileManager.setCurrentForPlayer(sender, profile)
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

		@Command(aliases = ["card"], desc = "Display a character's card in chat.")
		@Require(Permission.Command.Characters.Card)
		fun card(@Sender sender: CommandSender, @CommandTarget target: Character) {
			sender.sendMessage(characterCard(target))
		}

		@Command(aliases = ["kill"], desc = "Kill a character")
		@Require(Permission.Command.Characters.Kill)
		fun kill(@Sender sender: CommandSender, @CommandTarget target: Character) {
			if (target.isDead) {
				sender.sendError("This character is already dead")
				return
			}

			val owner = target.profile.owner

			if (sender != owner &&
					!(sender.hasPermission(Permission.Command.Characters.Kill + ".any") ||
							(sender.hasPermission(Permission.Staff) && owner == FABLES_ADMIN))) {
				sender.sendError("Permission denied")
			}

			target.isDead = true
			sender.sendMessage("$SYSPREFIX Killed ${target.name}")
		}

		@Command(aliases = ["resurrect"], desc = "Resurrect a character")
		@Require(Permission.Command.Characters.Resurrect)
		fun resurrect(@Sender sender: CommandSender, target: Character) {
			target.isDead = false
			sender.sendMessage("$SYSPREFIX Resurrected ${target.name}")
		}

		@Command(aliases = ["shelf"], desc = "Shelf a character")
		@Require(Permission.Command.Characters.Shelf)
		fun shelf(@Sender sender: Player, @CommandTarget target: Character, @Switch('f') force: Boolean) {
			if (target.isShelved) {
				sender.sendError("This character is already shelved")
				return
			}

			val owner = target.profile.owner

			if (sender != owner &&
					!(sender.hasPermission(Permission.Command.Characters.Shelf + ".any") ||
							(sender.hasPermission(Permission.Staff) && owner == FABLES_ADMIN))) {
				sender.sendError("Permission denied")
				return
			}

			if (force && !sender.hasPermission(Permission.Command.Characters.Shelf + ".force")) {
				sender.sendError("Permission denied")
				return
			}

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

		@Command(aliases = ["changename"], desc = "Change a character's name")
		@Require(Permission.Command.Characters.ChangeName)
		fun changename(@Sender sender: Player,
					   @CommandTarget(Permission.Command.Characters.ChangeName + ".others") target: Character) {
			plugin.launch {
				val oldName = target.name
				val newName = sender.promptChat("$SYSPREFIX Please enter ${oldName}'s new name:")

				if (newName.length > 32) {
					sender.sendError("Your character name cannot be longer than 32 characters")
					return@launch
				}

				if (characterRepository.nameExists(newName)) {
					sender.sendError("This name is already in use")
					return@launch
				}

				target.name = newName
				sender.sendMessage("$SYSPREFIX Changed $oldName's name to $newName")
			}
		}

		@Command(aliases = ["become"], desc = "Become a character")
		@Require(Permission.Command.Characters.Become)
		fun become(@Sender sender: Player,
				   @Optional @AllowCharacterName targetMaybe: Profile?,
				   @Optional @CommandTarget(Permission.Command.Characters.Become + ".others") who: Player,
				   @Switch('f') force: Boolean) {
			plugin.launch {
				val target: Profile = targetMaybe ?: run {
					val profiles = profileRepository.activeForOwner(who)
					profilePrompter.promptSelect(who, profiles)
				}

				val targetCharacter = characterRepository.forProfile(target)

				val owner = target.owner

				if (force && !sender.hasPermission(Permission.Command.Characters.Become + ".force")) {
					sender.sendError("Permission denied")
					return@launch
				}

				if (owner == FABLES_ADMIN && !sender.hasPermission(Permission.Staff)) {
					sender.sendError("You do not have permission to become a staff character.")
					return@launch
				} else if (owner != sender && !sender.hasPermission(Permission.Any)) {
					sender.sendError("You do not have permission to become a character that you don't own.")
					return@launch
				}

				if (targetCharacter?.isShelved == true) {
					sender.sendError("This character is currently shelved")
					return@launch
				}

				if (targetCharacter?.isDead == true) {
					sender.sendError("This character is dead.")
					return@launch
				}

				if (who == sender) {
					who.countdown(10U, emptyList(), listOf(CancelReason.MOVEMENT, CancelReason.HURT))
				}

				try {
					profileManager.setCurrentForPlayer(who, target, force)
				} catch (ex: ProfileOccupiedException) {
					sender.sendError("This character is currently occupied by ${ex.by.name}")
				}
			}
		}

		@Command(aliases = ["unshelf"], desc = "Unshelf a character")
		@Require(Permission.Command.Characters.Unshelf)
		fun unshelf(@Sender sender: CommandSender,
				  @CommandTarget(Permission.Command.Characters.Unshelf + ".others") target: Character,
					@Switch('f') force: Boolean) {
			if (force && !sender.hasPermission(Permission.Command.Characters.Unshelf + ".force")) {
				sender.sendError("Permission denied")
				return
			}

			if (!target.isShelved) {
				sender.sendError("${target.name} is not shelved.")
				return
			}

			val daysLeft = 21 - Duration.between(target.shelvedAt, Instant.now()).toDays()
			if (!force && daysLeft > 0) {
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
					@CommandTarget target: Character) {
				if (!(target.profile.owner == FABLES_ADMIN && sender.hasPermission(Permission.Staff))
						&& !sender.hasPermission(Permission.Command.Characters.Stats.Set + ".others")) {
					sender.sendError("You don't have permission to set the stats of this character")
					return
				}

				target.stats = target.stats.with(stat, value.toUInt())
				sender.sendMessage("$SYSPREFIX Set ${target.name}'s ${stat.toString().lowercase()} to $value")
			}

			@Command(aliases = ["edit"], desc = "Edit character stats")
			@Require(Permission.Command.Characters.Stats.Edit)
			fun edit(@Sender sender: Player,
					@CommandTarget(Permission.Command.Characters.Stats.Edit + ".others") target: Character) {
				val minimums = target.race.boosters + CHARACTER_STATS_FLOOR
				var initialSliderValues = target.stats
				if (initialSliderValues.strength > 8U ||
						initialSliderValues.defense > 8U ||
						initialSliderValues.agility > 8U ||
						initialSliderValues.intelligence > 8u) {
					sender.sendMessage("$SYSPREFIX Detected that you will be editing legacy player stats, starting with a clean slate.")
					initialSliderValues = CharacterStats(0U, 0U, 0U, 0U)
				}

				val gui = CharacterStatsGui(plugin, minimums, "#${target.id} ${target.name}'s stats",
						initialSliderValues)

				plugin.launch { target.stats = gui.execute(sender) }
			}
		}
	}
}
