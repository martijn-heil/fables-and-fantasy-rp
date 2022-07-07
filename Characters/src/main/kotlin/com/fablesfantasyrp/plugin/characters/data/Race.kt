package com.fablesfantasyrp.plugin.characters.data

enum class Race(private val displayName: String, val boosters: CharacterStats) {
	HUMAN("Human", CharacterStats(strength = 1U, defense = 1U, agility = 1U)),
	HIGH_ELF("High Elf", CharacterStats(agility = 1U, intelligence = 2U)),
	DARK_ELF("Dark Elf", CharacterStats(strength = 2U, intelligence = 1U)),
	WOOD_ELF("Wood Elf", CharacterStats(agility = 3U)),
	DWARF("Dwarf", CharacterStats(strength = 1U, defense = 2U)),
	TIEFLING("Tiefling", CharacterStats(intelligence = 3U)),
	ORC("Orc", CharacterStats(strength = 3U)),
	GOBLIN("Goblin", CharacterStats(defense = 1U, intelligence = 2U)),
	HALFLING("Halfling", CharacterStats(defense = 1U, agility = 1U, intelligence = 1U));

	override fun toString() = displayName
}
