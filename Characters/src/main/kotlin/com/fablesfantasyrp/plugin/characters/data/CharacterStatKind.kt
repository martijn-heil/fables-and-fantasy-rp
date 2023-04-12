package com.fablesfantasyrp.plugin.characters.data

enum class CharacterStatKind {
	STRENGTH,
	DEFENSE,
	AGILITY,
	INTELLIGENCE;

	fun getRollModifierFor(statValue: UInt): Int = (statValue.toInt() / 2 * 2 - 6) / 2
	override fun toString() = name.lowercase()
}
