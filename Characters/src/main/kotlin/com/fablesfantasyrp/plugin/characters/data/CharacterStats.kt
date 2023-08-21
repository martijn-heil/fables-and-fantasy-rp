package com.fablesfantasyrp.plugin.characters.data

import kotlin.math.max

val CHARACTER_STATS_FLOOR = CharacterStats(2U, 2U, 2U, 2U)

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

	fun with(kind: CharacterStatKind, value: UInt): CharacterStats = when (kind) {
		CharacterStatKind.STRENGTH -> this.copy(strength = value)
		CharacterStatKind.DEFENSE -> this.copy(defense = value)
		CharacterStatKind.AGILITY -> this.copy(agility = value)
		CharacterStatKind.INTELLIGENCE -> this.copy(intelligence = value)
	}

	operator fun plus(other: CharacterStats) = CharacterStats(
			strength = other.strength + this.strength,
			defense = other.defense + this.defense,
			agility = other.agility + this.agility,
			intelligence = other.intelligence + this.intelligence
	)

	operator fun minus(other: CharacterStats) = CharacterStats(
			strength = max(0, this.strength.toInt() - other.strength.toInt()).toUInt(),
			defense = max(0, this.defense.toInt() - other.defense.toInt()).toUInt(),
			agility = max(0, this.agility.toInt() - other.agility.toInt()).toUInt(),
			intelligence = max(0, this.intelligence.toInt() - other.intelligence.toInt()).toUInt(),
	)

	fun withModifiers(modifiers: Iterable<CharacterStatsModifier>) = CharacterStats(
		strength = max(0, this.strength.toInt() + modifiers.sumOf { it.strength }).toUInt(),
		defense = max(0, this.defense.toInt() + modifiers.sumOf { it.defense }).toUInt(),
		agility = max(0, this.agility.toInt() + modifiers.sumOf { it.agility }).toUInt(),
		intelligence = max(0, this.intelligence.toInt() + modifiers.sumOf { it.intelligence}).toUInt(),
	)
}

data class CharacterStatsModifier(val strength: Int = 0,
							  	val defense: Int = 0,
							  	val agility: Int = 0,
							  	val intelligence: Int = 0) {
	operator fun get(kind: CharacterStatKind) = when (kind) {
		CharacterStatKind.STRENGTH -> strength
		CharacterStatKind.DEFENSE -> defense
		CharacterStatKind.AGILITY -> agility
		CharacterStatKind.INTELLIGENCE -> intelligence
	}

	fun with(kind: CharacterStatKind, value: Int): CharacterStatsModifier = when (kind) {
		CharacterStatKind.STRENGTH -> this.copy(strength = value)
		CharacterStatKind.DEFENSE -> this.copy(defense = value)
		CharacterStatKind.AGILITY -> this.copy(agility = value)
		CharacterStatKind.INTELLIGENCE -> this.copy(intelligence = value)
	}

	operator fun plus(other: CharacterStatsModifier) = CharacterStatsModifier(
		strength = other.strength + this.strength,
		defense = other.defense + this.defense,
		agility = other.agility + this.agility,
		intelligence = other.intelligence + this.intelligence
	)

	operator fun minus(other: CharacterStats) = CharacterStatsModifier(
		strength = this.strength - other.strength.toInt(),
		defense = this.defense - other.defense.toInt(),
		agility = this.agility - other.agility.toInt(),
		intelligence = this.intelligence - other.intelligence.toInt(),
	)
}
