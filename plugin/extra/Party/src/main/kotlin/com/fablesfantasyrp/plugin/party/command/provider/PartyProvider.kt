package com.fablesfantasyrp.plugin.party.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.party.PartySpectatorManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository
import com.fablesfantasyrp.plugin.party.data.entity.Party
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import kotlinx.coroutines.runBlocking
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PartyProvider(private val parties: PartyRepository,
					private val characters: CharacterRepository,
					private val profileManager: ProfileManager,
					private val spectatorManager: PartySpectatorManager) : Provider<Party> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Party {
		return runBlocking {
			val isCommandTarget = modifiers.any{ it is CommandTarget }
			val sender = BukkitSenderProvider(CommandSender::class.java).get(arguments, modifiers)!!

			return@runBlocking if (isCommandTarget && !arguments.hasNext() && sender is Player) {
				val character = profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
				val spectatorTarget = spectatorManager.getParty(sender)
				if (character == null && spectatorTarget == null) {
					arguments.next()
					throw IllegalStateException()
				}
				val party = character?.let { parties.forMember(it) } ?: spectatorTarget
				if (party == null) {
					arguments.next()
					throw IllegalStateException()
				}
				party
			} else if (arguments.peek().startsWith("#")) {
				val id = arguments.next().removePrefix("#").toIntOrNull() ?: throw ArgumentParseException("Could not parse id")
				parties.forId(id) ?: throw ArgumentParseException("A party with id #$id could not be found.")
			} else {
				val name = arguments.next()
				parties.forName(name) ?: throw ArgumentParseException("A party with name '$name' could not be found.")
			}
		}
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return parties.allNames().asSequence()
			.filter { it.startsWith(prefix.removePrefix("\""), true) }
			.map { quoteCommandArgument(it) }
			.toList()
	}
}
