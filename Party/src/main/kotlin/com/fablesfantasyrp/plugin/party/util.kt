package com.fablesfantasyrp.plugin.party

import com.fablesfantasyrp.plugin.glowing.GlowingManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository

fun pickRandomPartyColor(parties: PartyRepository, glowingManager: GlowingManager): PartyColor {
	val usedColors = parties.all().mapNotNull { it.color }.toSet()
	val defaultColor = PartyColor.values().find { it.chatColor == glowingManager.defaultGlowColor }!!

	return PartyColor.values().toList()
		.minus(usedColors)
		.minus(defaultColor)
		.randomOrNull() ?: PartyColor.values().toList().minus(defaultColor).random()
}
