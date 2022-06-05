package com.fablesfantasyrp.plugin.characters

data class CharacterStats(	val strength: UInt,
						  	val defense: UInt,
							val agility: UInt,
							val intelligence: UInt) {
	operator fun get(kind: CharacterStatKind) = when (kind) {
		CharacterStatKind.STRENGTH -> strength
		CharacterStatKind.DEFENSE -> defense
		CharacterStatKind.AGILITY -> agility
		CharacterStatKind.INTELLIGENCE -> intelligence
	}
}
