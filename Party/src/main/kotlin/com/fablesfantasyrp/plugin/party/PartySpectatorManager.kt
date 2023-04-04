package com.fablesfantasyrp.plugin.party

import com.fablesfantasyrp.plugin.party.data.entity.Party
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*

class PartySpectatorManager(private val server: Server) {
	private val spectators = HashMap<UUID, Party>()
	private val byParty = HashMap<Party, MutableSet<UUID>>()

	fun getSpectators(party: Party): Collection<Player>
		= byParty[party]?.mapNotNull { server.getPlayer(it) } ?: emptyList()

	fun getParty(spectator: Player): Party? = spectators[spectator.uniqueId]

	fun spectate(party: Party, spectator: Player) {
		val oldParty = spectators.remove(spectator.uniqueId)
		if (oldParty != null) byParty[oldParty]?.remove(spectator.uniqueId)

		spectators[spectator.uniqueId] = party
		byParty.computeIfAbsent(party) { HashSet() }.add(spectator.uniqueId)
		spectator.sendMessage("$SYSPREFIX You are now spectating ${party.name}.")
	}

	fun stopSpectating(spectator: Player) {
		val party = spectators.remove(spectator.uniqueId)
		if (party != null) byParty[party]?.remove(spectator.uniqueId)
		spectator.sendMessage("$SYSPREFIX You are now no longer spectating any party.")
	}
}
