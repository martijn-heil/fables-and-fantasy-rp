package com.fablesfantasyrp.plugin.characters.data

enum class Race(private val displayName: String, val boosters: CharacterStats) {
	ATTIAN_HUMAN("Attian Human", CharacterStats(strength = 1U, defense = 1U, agility = 1U, intelligence = 1U)),
	HINTERLANDER_HUMAN("Hinterlander Human", CharacterStats(strength = 1U, defense = 2U, intelligence = 1U)),
	KHADAN_HUMAN("Khadan Human", CharacterStats(strength = 2U, agility = 2U)),
	HIGH_ELF("High Elf", CharacterStats(intelligence = 4U)),
	DARK_ELF("Dark Elf", CharacterStats(strength = 2U, intelligence = 2U)),
	WOOD_ELF("Wood Elf", CharacterStats(strength = 1U, agility = 3U)),
	DWARF("Dwarf", CharacterStats(defense = 4U)),
	TIEFLING("Tiefling", CharacterStats(strength = 1U, intelligence = 3U)),
	ORC("Orc", CharacterStats(strength = 3U, defense = 1U)),
	GOBLIN("Goblin", CharacterStats(defense = 1U, intelligence = 1U, agility = 2U)),
	HALFLING("Halfling", CharacterStats(defense = 1U, agility = 3U)),
	SYLVANI("Sylvani", CharacterStats( agility = 2U, intelligence = 3U)),
	OTHER("Other", CharacterStats(defense = 0U, agility = 0U, intelligence = 0U)),

	@Deprecated("legacy race")
	HUMAN("Human", CharacterStats(strength = 1U, defense = 1U, agility = 1U));

	override fun toString() = displayName
}
