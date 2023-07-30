package com.fablesfantasyrp.plugin.charactertraits.domain.repository

import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.charactertraits.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.charactertraits.domain.mapper.CharacterTraitMapper
import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.utils.withLock
import java.util.*

class CharacterTraitRepositoryImpl(child: CharacterTraitMapper)
	: MassivelyCachingEntityRepository<String, CharacterTrait, CharacterTraitMapper>(child),
	CharacterTraitRepository {

	private val byRace = HashMap<Race, MutableSet<CharacterTrait>>()
	private val byCharacter = WeakHashMap<Character, MutableSet<CharacterTrait>>()
	private val byCharacterDirty = HashMap<Character, MutableSet<CharacterTrait>>()

	override fun init() {
		super.init()

		Race.values().forEach { byRace[it] = child.forRace(it).map { forId(it.id)!! }.toMutableSet() }
	}

	override fun forRace(race: Race): Collection<CharacterTrait> {
		return lock.readLock().withLock { byRace[race]!! }
	}

	override fun forCharacter(character: Character): Collection<CharacterTrait> {
		return lock.readLock().withLock { byCharacter[character] } ?: run {
			lock.writeLock().withLock {
				byCharacter[character] ?: run {
					val traits = child.forCharacter(character).toMutableSet()
					byCharacter[character] = traits
					traits
				}
			}
		}
	}

	override fun linkToRace(race: Race, trait: CharacterTrait) {
		lock.writeLock().withLock {
			byRace[race]!!.add(trait)
		}
	}

	override fun unlinkFromRace(race: Race, trait: CharacterTrait) {
		lock.writeLock().withLock {
			byRace[race]!!.remove(trait)
		}
	}

	override fun linkToCharacter(character: Character, trait: CharacterTrait) {
		lock.writeLock().withLock {
			byCharacterDirty.computeIfAbsent(character) {
				byCharacter[character] ?: run {
					val traits = child.forCharacter(character).toMutableSet()
					byCharacter[character] = traits
					traits
				}
			}.add(trait)
		}
	}

	override fun unlinkFromCharacter(character: Character, trait: CharacterTrait) {
		lock.writeLock().withLock {
			byCharacterDirty.computeIfAbsent(character) {
				byCharacter[character] ?: run {
					val traits = child.forCharacter(character).toMutableSet()
					byCharacter[character] = traits
					traits
				}
			}.remove(trait)
		}
	}

	override fun saveAllDirty() {
		super.saveAllDirty()
		lock.readLock().withLock {
			for (race in Race.values()) {
				val persisted = child.forRace(race).toSet()
				val here = byRace[race]!!
				val deleted = persisted.minus(here)
				val created = here.minus(persisted)

				deleted.forEach { child.unlinkFromRace(race, it) }
				created.forEach { child.linkToRace(race, it) }
			}

			for ((character, here) in byCharacterDirty) {
				val persisted = child.forCharacter(character)

				val deleted = persisted.minus(here)
				val created = here.minus(persisted.toSet())

				deleted.forEach { child.unlinkFromCharacter(character, it) }
				created.forEach { child.linkToCharacter(character, it) }
			}
		}
	}
}
