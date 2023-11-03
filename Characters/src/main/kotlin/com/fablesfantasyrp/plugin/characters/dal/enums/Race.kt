package com.fablesfantasyrp.plugin.characters.dal.enums

import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier

enum class Race(private val displayName: String,
				val boosters: CharacterStatsModifier,
				val medianAge: Int?,
				val creatureSize: CreatureSize = CreatureSize.MEDIUM) {
	ATTIAN_HUMAN("Attian Human", CharacterStatsModifier(strength = 1, defense = 1, agility = 1, intelligence = 1), 80),
	HINTERLANDER_HUMAN("Hinterlander Human", CharacterStatsModifier(strength = 1, defense = 2, intelligence = 1), 80),
	KHADAN_HUMAN("Khadan Human", CharacterStatsModifier(strength = 2, agility = 2), 80),
	HIGH_ELF("High Elf", CharacterStatsModifier(intelligence = 4), 450),
	DARK_ELF("Dark Elf", CharacterStatsModifier(strength = 2, intelligence = 2), 450),
	WOOD_ELF("Wood Elf", CharacterStatsModifier(strength = 1, agility = 3), 350),
	DWARF("Dwarf", CharacterStatsModifier(defense = 4), 65, CreatureSize.SMALL),
	TIEFLING("Tiefling", CharacterStatsModifier(strength = 1, intelligence = 3), 120),
	ORC("Orc", CharacterStatsModifier(strength = 3, defense = 1), 150, CreatureSize.LARGE),
	GOBLIN("Goblin", CharacterStatsModifier(defense = 1, intelligence = 1, agility = 2), 130, CreatureSize.SMALL),
	HALFLING("Halfling", CharacterStatsModifier(defense = 1, agility = 3), 80, CreatureSize.SMALL),
	SYLVANI("Sylvani", CharacterStatsModifier( agility = 2, intelligence = 4), null),
	OTHER("Other", CharacterStatsModifier(defense = 0, agility = 0, intelligence = 0), null),

	@Deprecated("legacy race")
	HUMAN("Human", CharacterStatsModifier(strength = 1, defense = 1, agility = 1), 65);

	override fun toString() = displayName
}
