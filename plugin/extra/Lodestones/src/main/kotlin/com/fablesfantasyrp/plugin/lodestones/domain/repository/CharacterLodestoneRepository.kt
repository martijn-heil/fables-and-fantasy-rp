package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone

interface CharacterLodestoneRepository {
	fun forCharacter(character: Character): Set<Lodestone>
	fun add(character: Character, lodestone: Lodestone)
	fun remove(character: Character, lodestone: Lodestone)
}
