package com.fablesfantasyrp.plugin.characters.data

enum class CharacterStatKind {
	STRENGTH,
	DEFENSE,
	AGILITY,
	INTELLIGENCE;

	override fun toString() = name.lowercase()
}
