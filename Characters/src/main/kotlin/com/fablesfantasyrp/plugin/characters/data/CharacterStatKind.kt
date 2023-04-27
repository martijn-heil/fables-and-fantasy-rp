package com.fablesfantasyrp.plugin.characters.data

enum class CharacterStatKind(val shortName: String) {
	STRENGTH("str"),
	DEFENSE("def"),
	AGILITY("agil"),
	INTELLIGENCE("intel");

	fun getRollModifierFor(statValue: UInt): Int = (statValue.toInt() / 2 * 2 - 6) / 2
	override fun toString() = name.lowercase()
}
