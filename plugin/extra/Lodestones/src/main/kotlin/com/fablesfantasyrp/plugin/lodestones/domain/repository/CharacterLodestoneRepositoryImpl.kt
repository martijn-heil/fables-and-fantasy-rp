package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.mapper.CharacterLodestoneMapper
import java.util.*

class CharacterLodestoneRepositoryImpl(private val mapper: CharacterLodestoneMapper, private val lodestones: LodestoneRepository) : CharacterLodestoneRepository {
	private val byCharacter = WeakHashMap<Character, HashSet<Lodestone>>()
	private val strongCache = HashSet<Character>()

	fun init() {
		lodestones.onDestroy { lodestone ->
			byCharacter.values.forEach { it.remove(lodestone) }
			mapper.destroy(lodestone)
		}
	}

	override fun forCharacter(character: Character): Set<Lodestone> {
		return this.forCharacterMutable(character)
	}

	override fun add(character: Character, lodestone: Lodestone) {
		this.forCharacterMutable(character).add(lodestone)
		strongCache.add(character)
	}

	override fun remove(character: Character, lodestone: Lodestone) {
		this.forCharacterMutable(character).remove(lodestone)
		strongCache.add(character)
	}

	private fun forCharacterMutable(character: Character): MutableSet<Lodestone> {
		return byCharacter.computeIfAbsent(character) { mapper.forCharacter(character).toHashSet() }
	}

	fun saveAllDirty() {
		for (character in strongCache) {
			val lodestones = byCharacter[character]!!
			val persistedLodestones = mapper.forCharacter(character)

			val added = lodestones.subtract(persistedLodestones)
			val removed = persistedLodestones.subtract(lodestones)

			added.forEach { mapper.add(character, it) }
			removed.forEach { mapper.remove(character, it) }
		}
	}
}
