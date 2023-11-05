package com.fablesfantasyrp.plugin.lodestones.domain.mapper

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.lodestones.dal.repository.CharacterLodestoneDataRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository

class CharacterLodestoneMapper(private val lodestones: LodestoneRepository, private val child: CharacterLodestoneDataRepository) : CharacterLodestoneRepository {
	override fun forCharacter(character: Character): Set<Lodestone> = lodestones.forIds(child.forCharacter(character.id).asSequence()).toSet()
	override fun add(character: Character, lodestone: Lodestone) = child.add(character.id, lodestone.id)
	override fun remove(character: Character, lodestone: Lodestone) = child.remove(character.id, lodestone.id)
	fun destroy(lodestone: Lodestone) = child.destroy(lodestone.id)
}
