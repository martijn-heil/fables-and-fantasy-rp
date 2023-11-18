package com.fablesfantasyrp.plugin.characters.command

import com.fablesfantasyrp.plugin.characters.*
import com.fablesfantasyrp.plugin.characters.command.provider.AllowCharacterName
import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.dal.enums.Gender
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.CHARACTER_STATS_FLOOR
import com.fablesfantasyrp.plugin.characters.domain.CharacterStats
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.characters.event.CharacterChangeStatsEvent
import com.fablesfantasyrp.plugin.characters.gui.CharacterStatsGui
import com.fablesfantasyrp.plugin.form.YesNoChatPrompt
import com.fablesfantasyrp.plugin.form.promptChat
import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.gui.GuiSingleChoice
import com.fablesfantasyrp.plugin.gui.confirm
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
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.broadcast
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Optional
import com.sk89q.intake.parametric.annotation.Range
import com.sk89q.intake.parametric.annotation.Switch
import com.sk89q.intake.util.auth.AuthorizationException
import kotlinx.coroutines.async
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.GlobalContext
import java.time.Duration
import java.time.Instant
import kotlin.math.max


class LegacyCommands(private val plugin: Plugin, private val characterCommands: Commands.Characters) {
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
			   @Switch('f') force: Boolean,
			   @Switch('i') instant: Boolean) {
		characterCommands.become(sender, targetMaybe, who, force, instant)
	}

	@Command(aliases = ["card", "charactercard"], desc = "Display a character's card in chat.")
	@Require(Permission.Command.Characters.Card)
	fun card(@Sender sender: CommandSender, @CommandTarget target: Character) {
		characterCommands.card(sender, target)
	}

	@Command(aliases = ["removecharacter", "removechar", "permakill", "pk"], desc = "Kill a character")
	@Require(Permission.Command.Characters.Kill)
	fun removeCharacter(@Sender sender: CommandSender, @CommandTarget target: Character) {
		plugin.launch {
			if (sender is Player && !sender.confirm("Permanently kill ${target.name}?")) return@launch
			characterCommands.kill(sender, target)
		}
	}
}

class Commands(private val plugin: JavaPlugin,
			   private val characterRepository: CharacterRepository,
			   private val characterTraitRepository: CharacterTraitRepository,
			   private val profileRepository: ProfileRepository,
			   private val profileManager: ProfileManager,
			   private val profilePrompter: ProfilePrompter,
			   private val authorizer: CharacterAuthorizer) {
	private val server = plugin.server
	private val characterCardGenerator by lazy { GlobalContext.get().get<CharacterCardGenerator>() }

	inner class Characters {
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
					info = promptNewCharacterInfo(sender, getAllowedRaces(isStaffCharacter))

					if (characterRepository.nameExists(info.name)) {
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
						dateOfBirth = info.dateOfBirth,
						dateOfNaturalDeath = null,
						description = info.description,
						gender = info.gender,
						race = info.race,
						stats = info.stats,
						profile = profile,
						createdAt = Instant.now()))

				info.traits.forEach { characterTraitRepository.linkToCharacter(character, it) }

				profileManager.setCurrentForPlayer(sender, profile)
			}
		}

		@Command(aliases = ["list"], desc = "List characters owned by player")
		@Require(Permission.Command.Characters.List)
		fun list(@Sender sender: CommandSender,
				@CommandTarget(Permission.Command.Characters.List + ".others") owner: OfflinePlayer) {
			val message = legacyText("$SYSPREFIX ${owner.name} has the following characters:")
				.append(Component.newline())
				.append(
					Component.join(JoinConfiguration.newlines(), characterRepository.forOwner(owner).map {
						val dead = if (it.isDead) " ${ChatColor.RED}(dead)" else ""
						val shelved = if (it.isShelved) " ${ChatColor.YELLOW}(shelved)" else ""
						legacyText("${ChatColor.GRAY}#${it.id} ${it.name}${dead}${shelved}")
					}))
			sender.sendMessage(message)
		}

		@Command(aliases = ["listunowned"], desc = "List characters without an owner")
		@Require(Permission.Command.Characters.Listunowned)
		fun listunowned(@Sender sender: CommandSender) {
			sender.sendMessage(legacyText("$SYSPREFIX The following characters have no owner:").append(
			Component.join(JoinConfiguration.newlines(), characterRepository.forOwner(null).map {
				val dead = if (it.isDead) " ${ChatColor.RED}(dead)" else ""
				val shelved = if (it.isShelved) " ${ChatColor.YELLOW}(shelved)" else ""
				legacyText("${ChatColor.GRAY}#${it.id} ${it.name}${dead}${shelved}")
			})))
		}

		@Command(aliases = ["card"], desc = "Display a character's card in chat.")
		@Require(Permission.Command.Characters.Card)
		fun card(@Sender sender: CommandSender, @CommandTarget target: Character) {
			sender.sendMessage(characterCardGenerator.card(target, sender))
		}

		@Command(aliases = ["kill"], desc = "Kill a character")
		@Require(Permission.Command.Characters.Kill)
		fun kill(@Sender sender: CommandSender, @CommandTarget target: Character) {
			if (target.isDead) {
				sender.sendError("This character is already dead")
				return
			}

			authorizer.mayEdit(sender, target).orElse { throw AuthorizationException(it) }

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
		fun shelf(@Sender sender: Player, @CommandTarget target: Character, @Switch('f') force: Boolean, @Switch('y') yes: Boolean) {
			if (target.isShelved) {
				sender.sendError("This character is already shelved")
				return
			}

			val owner = target.profile.owner
			authorizer.mayEdit(sender, target).orElse { throw AuthorizationException(it) }

			if (force && !sender.hasPermission(Permission.Command.Characters.Shelf + ".force")) {
				sender.sendError("Permission denied")
				return
			}

			if (owner != null && !target.isStaffCharacter) {
				val shelved = characterRepository.forOwner(owner).filter { it.isShelved && !it.isDead }.size
				if (shelved >= 3) {
					sender.sendError("You cannot shelve more than 3 characters!")
					return
				}
			}

			plugin.launch {
				if (!yes) {
					val prompt = YesNoChatPrompt(sender, miniMessage.deserialize(
							"<prefix> Are you sure you want to shelf <green><character_name></green>?<newline>" +
									"You will not be able to unshelf this character for 3 weeks.",
							Placeholder.component("prefix", legacyText(SYSPREFIX)),
							Placeholder.unparsed("character_name", target.name)).color(NamedTextColor.GRAY))
					prompt.send()
					val confirmation: Boolean = prompt.await()
					if (!confirmation) return@launch
				}

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

		@Command(aliases = ["become", "switch"], desc = "Become a character")
		@Require(Permission.Command.Characters.Become)
		fun become(@Sender sender: Player,
				   @Optional @AllowCharacterName targetMaybe: Profile?,
				   @Optional @CommandTarget(Permission.Command.Characters.Become + ".others") who: Player,
				   @Switch('f') force: Boolean,
				   @Switch('i') instant: Boolean) {
			if (!sender.hasPermission(Permission.Command.Characters.Become + ".instant")) {
				sender.sendError("Permission denied")
				return
			}

			plugin.launch {
				val target: Profile = targetMaybe ?: run {
					val profiles = profileRepository.activeForOwner(who)
					profilePrompter.promptSelect(who, profiles)
				}

				authorizer.mayBecome(sender, target).orElse { sender.sendError(it); return@launch }

				if (!instant && who == sender) {
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
					@CommandTarget target: Character,
					@Switch('f') force: Boolean) {
			if (force && !sender.hasPermission(Permission.Command.Characters.Unshelf + ".force")) {
				sender.sendError("Permission denied")
				return
			}

			authorizer.mayEdit(sender, target, allowShelved = true).orElse { throw AuthorizationException(it) }

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

		@Command(aliases = ["transfer"], desc = "Transfer a character to another player")
		@Require(Permission.Command.Characters.Transfer)
		fun transfer(@Sender sender: CommandSender, to: Player, @CommandTarget target: Character) {
			authorizer.mayTransfer(sender, target).orElse { throw AuthorizationException(it) }

			plugin.launch {
				if (sender is Player) {
					if (!sender.confirm("Transfer ${target.shortName} to ${to.name}?")) return@launch
				}

				target.profile.owner = to
				sender.sendMessage("$SYSPREFIX Transferred ownership of ${target.name} to ${to.name}")

				if (sender is Player) {
					if (!authorizer.mayBecome(sender, target).result &&
						profileManager.getCurrentForPlayer(sender) == target.profile) {
						profileManager.stopTracking(sender)
					}
				}
			}
		}

		inner class Stats {
			@Command(aliases = ["set"], desc = "Set character stats")
			@Require(Permission.Command.Characters.Stats.Set)
			fun set(@Sender sender: CommandSender,
					stat: CharacterStatKind,
					@Range(min = 0.0, max = 127.0) value: Int,
					@CommandTarget target: Character) {
				authorizer.mayEditStats(sender, target).orElse { throw AuthorizationException(it) }

				target.stats = target.stats.with(stat, value.toUInt())
				sender.sendMessage("$SYSPREFIX Set ${target.name}'s ${stat.toString().lowercase()} to $value")
			}

			@Command(aliases = ["edit"], desc = "Edit character stats")
			@Require(Permission.Command.Characters.Stats.Edit)
			fun edit(@Sender sender: Player, target: Character) {
				val minimums = CHARACTER_STATS_FLOOR.withModifiers(listOf(target.race.boosters))
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

		inner class Change {
			@Command(aliases = ["dateofbirth"], desc = "Set a character's date of birth")
			fun dateofbirth(@Sender sender: Player, @CommandTarget target: Character) {
				authorizer.mayEditDateOfBirth(sender, target).orElse { throw AuthorizationException(it) }

				plugin.launch {
					val newDate = promptDateOfBirth(sender)
					target.dateOfBirth = newDate
					sender.sendMessage("$SYSPREFIX Date of birth changed!")
				}
			}

			@Command(aliases = ["description"], desc = "Change a character's description")
			fun description(@Sender sender: Player, @CommandTarget target: Character) {
				authorizer.mayEditDescription(sender, target).orElse { throw AuthorizationException(it) }

				plugin.launch {
					val newDescription = sender.promptChat("$SYSPREFIX Please enter ${target.name}'s new description:")
					target.description = newDescription
					sender.sendMessage("$SYSPREFIX Description changed!")
				}
			}

			@Command(aliases = ["name"], desc = "Change a character's name")
			fun name(@Sender sender: Player, @CommandTarget target: Character) {
				authorizer.mayEditName(sender, target).orElse { throw AuthorizationException(it) }

				plugin.launch {
					val oldName = target.name
					val newName = sender.promptChat("$SYSPREFIX Please enter ${oldName}'s new name:")

					if (newName.length > 32) {
						sender.sendError("Your character name cannot be longer than 32 characters")
						return@launch
					}

					if (NAME_DISALLOWED_CHARACTERS.containsMatchIn(newName)) {
						sender.sendError("Your character name contains illegal characters, please only use alphanumeric characters and single quotes.")
						return@launch
					}

					if (newName.isBlank()) {
						sender.sendError("Your character name is blank")
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

			@Command(aliases = ["stats"], desc = "Change a character's stats")
			fun stats(@Sender sender: Player, @CommandTarget target: Character) {
				authorizer.mayEditStats(sender, target).orElse { throw AuthorizationException(it) }

				if (!target.isStaffCharacter && target.changedStatsAt != null) {
					val daysLeft = max(0, 30 - Duration.between(target.changedStatsAt, Instant.now()).toDays())
					if (daysLeft > 0) {
						sender.sendError("You must wait $daysLeft more days before you can change your stats again!")
						return
					}
				}

				val minimums = CHARACTER_STATS_FLOOR.withModifiers(listOf(target.race.boosters))
				var initialSliderValues = target.stats
				if (initialSliderValues.strength > 8U ||
						initialSliderValues.defense > 8U ||
						initialSliderValues.agility > 8U ||
						initialSliderValues.intelligence > 8u) {
					sender.sendMessage("$SYSPREFIX Detected that you will be editing legacy player stats, starting with a clean slate.")
					initialSliderValues = CharacterStats(0U, 0U, 0U, 0U)
				}

				val gui = CharacterStatsGui(plugin, minimums, "#${target.id} ${target.name}'s stats", initialSliderValues)
				plugin.launch {
					val oldStats = target.stats
					val newStats = gui.execute(sender)
					target.stats = newStats

					server.pluginManager.callEvent(CharacterChangeStatsEvent(target, oldStats, newStats))

					target.changedStatsAt = Instant.now()

					val player = profileManager.getCurrentForProfile(target.profile) ?: return@launch
					server.broadcast(player.location, 30, "$SYSPREFIX ${target.name} changed their stats!")
				}
			}

			@Command(aliases = ["race"], desc = "Change a character's race")
			fun race(@Sender sender: Player, @CommandTarget target: Character) {
				authorizer.mayEditRace(sender, target).orElse { throw AuthorizationException(it) }

				plugin.launch {
					val race: Race = sender.promptGui(GuiSingleChoice(plugin,
							"Please choose a new race",
							Race.values().asSequence(),
							{ it.itemStackRepresentation },
							{ "${ChatColor.GOLD}$it" }
					))
					target.race = race
					sender.sendMessage("$SYSPREFIX Changed ${target.name}'s race to $race")
				}
			}

			@Command(aliases = ["gender"], desc = "Change a character's gender")
			fun gender(@Sender sender: Player, @CommandTarget target: Character) {
				authorizer.mayEditGender(sender, target).orElse { throw AuthorizationException(it) }

				plugin.launch {
					val gender: Gender = sender.promptGui(GuiSingleChoice(FablesCharacters.instance,
							"Please choose a new gender",
							Gender.values().asSequence(),
							{
								ItemStack(when (it) {
									Gender.MALE -> Material.CYAN_WOOL
									Gender.FEMALE -> Material.PINK_WOOL
									Gender.OTHER -> Material.WHITE_WOOL
								})
							},
							{ "${ChatColor.GOLD}" + it.toString().replaceFirstChar { firstChar -> firstChar.uppercase() } }
					))
					target.gender = gender
					sender.sendMessage("$SYSPREFIX Changed ${target.name}'s gender to $gender")
				}
			}

			@Command(aliases = ["owner"], desc = "Change a character's owner")
			fun owner(@Sender sender: Player, @CommandTarget target: Character) {
				authorizer.mayTransfer(sender, target).orElse { throw AuthorizationException(it) }

				plugin.launch {
					val playerName = sender.promptChat(
						Component.text("Please enter the name of the player to transfer ${target.name} to:")
							.color(NamedTextColor.GRAY)
					)
					val player = async { server.getOfflinePlayer(playerName) }.await()

					if (!player.isOnline && !player.hasPlayedBefore()) {
						sender.sendError("Player not found.")
						return@launch
					}

					if (!sender.confirm("Transfer ${target.shortName} to ${player.name}?")) return@launch

					target.profile.owner = player
					sender.sendMessage("$SYSPREFIX Transferred ownership of ${target.name} to ${player.name}")

					if (!authorizer.mayBecome(sender, target).result &&
						profileManager.getCurrentForPlayer(sender) == target.profile) {
						profileManager.stopTracking(sender)
					}
				}
			}
		}
	}
}
