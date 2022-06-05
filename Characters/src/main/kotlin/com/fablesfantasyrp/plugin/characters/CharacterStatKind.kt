package com.fablesfantasyrp.plugin.characters

enum class CharacterStatKind {
	STRENGTH,
	DEFENSE,
	AGILITY,
	INTELLIGENCE;

	override fun toString() = name.lowercase()
}
