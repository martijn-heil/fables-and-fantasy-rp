package com.fablesfantasyrp.plugin.characters.data

import kotlin.math.min

enum class CharacterStatKind {
	STRENGTH,
	DEFENSE,
	AGILITY,
	INTELLIGENCE;

	fun getRollModifierFor(statValue: UInt): Int = (min(statValue, 12U).toInt() / 2 * 2 - 6) / 2
	override fun toString() = name.lowercase()
}
