package com.fablesfantasyrp.plugin.party

import com.fablesfantasyrp.plugin.glowing.GlowingManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository
import com.fablesfantasyrp.plugin.party.data.entity.Party
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

fun pickRandomPartyColor(parties: PartyRepository, glowingManager: GlowingManager): PartyColor {
	val usedColors = parties.all().mapNotNull { it.color }.toSet()

	return PartyColor.values().toList().minus(usedColors).randomOrNull() ?: PartyColor.values().random()
}

fun partyCard(party: Party): Component {
	return miniMessage.deserialize(
		"<newline>" +
			"<gray>═════ <white><name></white> <dark_gray>#<id></dark_gray> ═════</gray><newline>" +
			"<green>" +
			"Owner: <white><owner></white><newline>" +
			"Member count: <white><member_count></white><newline>" +
			"Spawn point: <white><spawn_point></white><newline>" +
			"Using respawns: <white><using_respawns></white><newline>" +
			"Respawns remaining: <white><respawns></white><newline>" +
			"</green>",
		Placeholder.unparsed("name", party.name),
		Placeholder.unparsed("id", party.id.toString()),
		Placeholder.unparsed("owner", party.owner.name),
		Placeholder.unparsed("member_count", party.members.size.toString()),
		Placeholder.unparsed("using_respawns", if (party.useRespawns) "Yes" else "No"),
		Placeholder.unparsed("respawns", party.respawns.toString()),
		Placeholder.unparsed("spawn_point", party.respawnLocation?.humanReadable() ?: "Not set")
	)
}
