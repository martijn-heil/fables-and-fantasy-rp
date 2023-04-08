package com.fablesfantasyrp.plugin.party

import com.fablesfantasyrp.plugin.glowing.GlowingManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository

fun pickRandomPartyColor(parties: PartyRepository, glowingManager: GlowingManager): PartyColor {
	val usedColors = parties.all().mapNotNull { it.color }.toSet()

	return PartyColor.values().toList().minus(usedColors).randomOrNull() ?: PartyColor.values().random()
}
