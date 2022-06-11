package com.fablesfantasyrp.plugin.characters

data class CharacterStats(	val strength: UInt = 0U,
						  	val defense: UInt = 0U,
							val agility: UInt = 0U,
							val intelligence: UInt = 0U) {
	operator fun get(kind: CharacterStatKind) = when (kind) {
		CharacterStatKind.STRENGTH -> strength
		CharacterStatKind.DEFENSE -> defense
		CharacterStatKind.AGILITY -> agility
		CharacterStatKind.INTELLIGENCE -> intelligence
	}

	operator fun plus(other: CharacterStats) = CharacterStats(
			strength = other.strength + this.strength,
			defense = other.defense + this.defense,
			agility = other.agility + this.agility,
			intelligence = other.intelligence + this.intelligence
	)
}
