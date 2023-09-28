package com.fablesfantasyrp.plugin.characters.dal.enums

import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier

enum class Race(private val displayName: String, val boosters: CharacterStatsModifier, val creatureSize: CreatureSize = CreatureSize.MEDIUM) {
	ATTIAN_HUMAN("Attian Human", CharacterStatsModifier(strength = 1, defense = 1, agility = 1, intelligence = 1)),
	HINTERLANDER_HUMAN("Hinterlander Human", CharacterStatsModifier(strength = 1, defense = 2, intelligence = 1)),
	KHADAN_HUMAN("Khadan Human", CharacterStatsModifier(strength = 2, agility = 2)),
	HIGH_ELF("High Elf", CharacterStatsModifier(intelligence = 4)),
	DARK_ELF("Dark Elf", CharacterStatsModifier(strength = 2, intelligence = 2)),
	WOOD_ELF("Wood Elf", CharacterStatsModifier(strength = 1, agility = 3)),
	DWARF("Dwarf", CharacterStatsModifier(defense = 4), CreatureSize.SMALL),
	TIEFLING("Tiefling", CharacterStatsModifier(strength = 1, intelligence = 3)),
	ORC("Orc", CharacterStatsModifier(strength = 3, defense = 1), CreatureSize.LARGE),
	GOBLIN("Goblin", CharacterStatsModifier(defense = 1, intelligence = 1, agility = 2), CreatureSize.SMALL),
	HALFLING("Halfling", CharacterStatsModifier(defense = 1, agility = 3), CreatureSize.SMALL),
	SYLVANI("Sylvani", CharacterStatsModifier( agility = 2, intelligence = 3)),
	OTHER("Other", CharacterStatsModifier(defense = 0, agility = 0, intelligence = 0)),

	@Deprecated("legacy race")
	HUMAN("Human", CharacterStatsModifier(strength = 1, defense = 1, agility = 1));

	override fun toString() = displayName
}
