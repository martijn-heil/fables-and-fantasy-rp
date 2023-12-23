package com.fablesfantasyrp.plugin.party.command

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.shortName
import com.fablesfantasyrp.plugin.party.frunBlocking
import com.fablesfantasyrp.plugin.glowing.GlowingManager
import com.fablesfantasyrp.plugin.party.*
import com.fablesfantasyrp.plugin.party.Permission
import com.fablesfantasyrp.plugin.party.auth.PartyAuthorizer
import com.fablesfantasyrp.plugin.party.data.PartyRepository
import com.fablesfantasyrp.plugin.party.data.entity.Party
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.command.annotation.AllowPlayerName
import com.fablesfantasyrp.plugin.targeting.data.SimpleTargetingPlayerDataRepository
import com.fablesfantasyrp.plugin.targeting.targetingPlayerDataRepository
import com.fablesfantasyrp.plugin.text.*
import com.fablesfantasyrp.plugin.utils.getPlayersWithinRange
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
import com.sk89q.intake.parametric.annotation.Optional
import com.sk89q.intake.parametric.annotation.Range
import com.sk89q.intake.util.auth.AuthorizationException
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class Commands(private val plugin: Plugin,
			   private val profileManager: ProfileManager,
			   private val parties: PartyRepository,
			   private val characters: CharacterRepository,
			   private val partyAuthorizer: PartyAuthorizer,
			   private val partySpectatorManager: PartySpectatorManager,
			   private val glowingManager: GlowingManager) {
	private val server = plugin.server
	private val targetingRepository: SimpleTargetingPlayerDataRepository = targetingPlayerDataRepository

	inner class PartyCommand {
		@Command(aliases = ["spectate"], desc = "Spectate a party")
		@Require(Permission.Command.Party.Spectate)
		fun spectate(@Sender sender: Player, @Optional party: Party?) {
			flaunch {
				val senderCharacter = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }

				if (senderCharacter != null && parties.forMember(senderCharacter) != null) {
					sender.sendError("You cannot spectate a party when you are a member of a party")
					return@flaunch
				}

				if (party != null) {
					partySpectatorManager.spectate(party, sender)
				} else {
					partySpectatorManager.stopSpectating(sender)
				}
			}
		}

		@Command(aliases = ["list"], desc = "List parties")
		@Require(Permission.Command.Party.List)
		fun list(@Sender sender: CommandSender) {
			val canSpectate = sender.hasPermission(Permission.Command.Party.Spectate)
			val message = legacyText("$SYSPREFIX Parties:")
				.append(Component.newline())
				.append(Component.join(JoinConfiguration.newlines(), parties.all().map {
					val spectateButton = if (canSpectate) Component.text(" [ SPECTATE ]")
						.color(NamedTextColor.GREEN)
						.decorate(TextDecoration.BOLD)
						.clickEvent(ClickEvent.runCommand("/party spectate #${it.id}"))
					else Component.empty()
					miniMessage.deserialize("<gray>#<id> <name> <dark_gray><i>(<owner>)</i></dark_gray><spectate></gray>",
						Placeholder.unparsed("id", it.id.toString()),
						Placeholder.component("name", legacyText("${it.color?.chatColor ?: ""}${it.name}")),
						Placeholder.unparsed("owner", it.owner.name),
						Placeholder.component("spectate", spectateButton))
				}))
			sender.sendMessage(message)
		}

		@Command(aliases = ["create"], desc = "Create a party")
		@Require(Permission.Command.Party.Create)
		fun create(@Sender sender: Character, @Optional name: String?) {
			val player = profileManager.getCurrentForProfile(sender.profile)!!
			if (parties.forMember(sender) != null) {
				player.sendError("You are already in a party")
				return
			}

			if (name != null && !isSaneName(name)) {
				player.sendError("The name \"$name\" contains illegal characters. Please choose a different name.")
				return
			}

			val finalName = name ?: "${sender.shortName}'s party"

			if (parties.forName(finalName) != null) {
				player.sendError("The default name \"$finalName\" for your party is already in use. " +
					"Please explicitly specify a name: /party create myname")
				return
			}

			val color = pickRandomPartyColor(parties, glowingManager)

			val party = parties.create(Party(
				owner = sender,
				name = finalName,
				color = color
			))
			party.updateGlow()

			player.sendMessage("$SYSPREFIX Created ${party.name}")
		}

		@Command(aliases = ["transfer"], desc = "Transfer a party")
		@Require(Permission.Command.Party.Transfer)
		fun transfer(@Sender sender: CommandSender, @AllowPlayerName to: Character, @CommandTarget party: Party) {
			partyAuthorizer.mayTransfer(party, sender).orElse { throw AuthorizationException(it) }

			if (!party.members.contains(to)) {
				sender.sendError("${to.name} is not a member of this party.")
				return
			}

			party.owner = to
		}

		@Command(aliases = ["name"], desc = "Change a party's name")
		@Require(Permission.Command.Party.Name)
		fun name(@Sender sender: CommandSender, name: String, @CommandTarget party: Party) {
			partyAuthorizer.mayRename(party, sender).orElse { throw AuthorizationException(it) }
			if (!isSaneName(name)) {
				sender.sendError("The name \"$name\" contains illegal characters. Please choose a different name.")
				return
			}

			if (parties.forName(name) != null) {
				sender.sendError("This name is already in use. Please choose a different name.")
				return
			}

			party.name = name
		}

		@Command(aliases = ["color"], desc = "Change a party's color")
		@Require(Permission.Command.Party.Color)
		fun color(@Sender sender: CommandSender, color: PartyColor, @CommandTarget party: Party) {
			party.color = color
		}

		@Command(aliases = ["setspawn"], desc = "Set a party spawn")
		@Require(Permission.Command.Party.Setspawn)
		fun setspawn(@Sender sender: Player, @CommandTarget party: Party) {
			party.respawnLocation = sender.location
		}

		@Command(aliases = ["setrespawns"], desc = "Set a party's respawn count")
		@Require(Permission.Command.Party.Setrespawns)
		fun setrespawns(@Sender sender: CommandSender, @Range(min = 0.00) respawns: Int, @CommandTarget party: Party) {
			party.respawns = respawns
		}

		@Command(aliases = ["togglerespawns"], desc = "Toggle whether a party uses their respawns")
		@Require(Permission.Command.Party.Togglerespawns)
		fun togglerespawns(@Sender sender: CommandSender, @CommandTarget party: Party) {
			party.useRespawns = !party.useRespawns
		}

		@Command(aliases = ["resetspawn"], desc = "Reset a party spawn")
		@Require(Permission.Command.Party.Setspawn)
		fun resetspawn(@Sender sender: CommandSender, @CommandTarget party: Party) {
			party.respawnLocation = null
		}

		@Command(aliases = ["invite"], desc = "Invite someone to a party")
		@Require(Permission.Command.Party.Invite)
		fun invite(@Sender sender: CommandSender, @AllowPlayerName who: Character, @CommandTarget party: Party) {
			partyAuthorizer.mayInviteMember(party, sender).orElse { throw AuthorizationException(it) }

			if (party.members.contains(who)) {
				sender.sendError("${who.name} is already a member of the party")
				return
			}

			if (party.invites.contains(who)) {
				sender.sendError("${who.name} is already invited to the party")
				return
			}

			party.invites = party.invites.plus(who)
		}

		@Command(aliases = ["invitenear"], desc = "Invite everyone within a specified radius to a party")
		@Require(Permission.Command.Party.InviteNear)
		fun invitenear(@Sender sender: Player,
					   @Optional("30") @Range(min = 0.00, max = 100.0) radius: Int,
					   @Optional @CommandTarget party: Party) {
			partyAuthorizer.mayInviteMember(party, sender).orElse { throw AuthorizationException(it) }
			flaunch {
				val characters = getPlayersWithinRange(sender.location, radius.toUInt())
					.asFlow()
					.mapNotNull { profileManager.getCurrentForPlayer(it) }
					.mapNotNull { characters.forProfile(it) }
					.filter { !party.members.contains(it) }
					.filter { !party.invites.contains(it) }
					.toList()

				party.invites = party.invites.plus(characters)
			}
		}

		@Command(aliases = ["uninvite"], desc = "Retract an invite to a party")
		@Require(Permission.Command.Party.Uninvite)
		fun uninvite(@Sender sender: CommandSender, @AllowPlayerName who: Character, @CommandTarget party: Party) {
			partyAuthorizer.mayInviteMember(party, sender).orElse { throw AuthorizationException(it) }

			if (!party.invites.contains(who)) {
				sender.sendError("${who.name} was not invited to the party")
				return
			}

			party.invites = party.invites.minus(who)
		}

		@Command(aliases = ["invites"], desc = "List invites of a party")
		@Require(Permission.Command.Party.Invites)
		fun invites(@Sender sender: CommandSender, @CommandTarget party: Party) {
			val message = legacyText("$SYSPREFIX ${party.name} pending invites: (${party.invites.size})")
				.append(Component.newline())
				.append(Component.text(party.owner.name).append(Component.text("⭐").color(NamedTextColor.GOLD)))
				.append(Component.newline())
				.append(Component.join(JoinConfiguration.newlines(), party.invites
					.filter { it != party.owner }
					.map { Component.text(it.name) })).color(NamedTextColor.GRAY)

			sender.sendMessage(message)
		}

		@Command(aliases = ["join"], desc = "Join a party")
		@Require(Permission.Command.Party.Join)
		fun join(@Sender sender: CommandSender,
				 party: Party,
				 @CommandTarget(Permission.Command.Party.Join + ".others") who: Character) {
			val senderCharacter = (sender as? Player)
				?.let { profileManager.getCurrentForPlayer(it) }
				?.let { frunBlocking { characters.forProfile(it) } }

			if (senderCharacter == who) {
				partyAuthorizer.mayJoin(party, who).orElse { throw AuthorizationException(it) }
			}

			if (parties.forMember(who) != null) {
				if (senderCharacter == who) {
					sender.sendError("You are already in a party")
				} else {
					sender.sendError("${who.name} is already in a party")
				}
				return
			}

			party.members = party.members.plus(who)
		}

		@Command(aliases = ["leave"], desc = "Leave your party")
		@Require(Permission.Command.Party.Leave)
		fun leave(@Sender sender: Character) {
			val player = profileManager.getCurrentForProfile(sender.profile)!!
			val party = parties.forMember(sender) ?: run {
				player.sendError("You are not member of any party")
				return
			}

			if (sender == party.owner) {
				partyAuthorizer.mayDisband(party, sender).orElse { throw AuthorizationException(it) }
				parties.destroy(party)
			} else {
				party.members = party.members.minus(sender)
			}
		}

		@Command(aliases = ["disband"], desc = "Disband a party")
		@Require(Permission.Command.Party.Disband)
		fun disband(@Sender sender: CommandSender, @CommandTarget party: Party) {
			partyAuthorizer.mayDisband(party, sender).orElse { throw AuthorizationException(it) }
			parties.destroy(party)
		}

		@Command(aliases = ["kick"], desc = "Kick a member from a party")
		@Require(Permission.Command.Party.Kick)
		fun kick(@Sender sender: CommandSender, @AllowPlayerName who: Character, @CommandTarget party: Party) {
			partyAuthorizer.mayKickMember(party, sender, who).orElse { throw AuthorizationException(it) }
			party.members = party.members.minus(who)
		}

		@Command(aliases = ["members"], desc = "List members of a party")
		@Require(Permission.Command.Party.Members)
		fun members(@Sender sender: CommandSender, @CommandTarget party: Party) {
			val message = legacyText("$SYSPREFIX ${party.name} members: (${party.members.size})")
				.append(Component.newline())
				.append(Component.text(party.owner.name).append(Component.text("⭐").color(NamedTextColor.GOLD)))
				.append(Component.newline())
				.append(Component.join(JoinConfiguration.newlines(), party.members
					.filter { it != party.owner }
					.map { Component.text(it.name) })).color(NamedTextColor.GRAY)

			sender.sendMessage(message)
		}

		@Command(aliases = ["card"], desc = "Display a party's card")
		@Require(Permission.Command.Party.Card)
		fun card(@Sender sender: CommandSender, @CommandTarget party: Party) {
			sender.sendMessage(partyCard(party))
		}

		@Command(aliases = ["select"], desc = "Select a party for targeting")
		@Require(Permission.Command.Party.Select)
		fun select(@Sender sender: Player, @CommandTarget party: Party) {
			val players = party.members.mapNotNull { profileManager.getCurrentForProfile(it.profile) }
			val data = targetingRepository.forOfflinePlayer(sender)
			targetingRepository.update(data.copy(targets = data.targets.plus(players)))
		}
	}
}
