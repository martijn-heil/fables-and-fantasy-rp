package com.fablesfantasyrp.plugin.characters.domain.repository

import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.mapper.CharacterTraitMapper
import com.fablesfantasyrp.plugin.characters.event.CharacterChangeTraitsEvent
import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.utils.withLock
import org.bukkit.Server
import java.util.*

class CharacterTraitRepositoryImpl(private val server: Server, child: CharacterTraitMapper)
	: MassivelyCachingEntityRepository<String, CharacterTrait, CharacterTraitMapper>(child),
	CharacterTraitRepository {

	private val byRace = HashMap<Race, HashSet<CharacterTrait>>()
	private val byCharacter = WeakHashMap<Character, HashSet<CharacterTrait>>()
	private val byCharacterDirty = HashMap<Character, HashSet<CharacterTrait>>()

	override fun init() {
		super.init()

		Race.values().forEach { byRace[it] = child.forRace(it).map { forId(it.id)!! }.toHashSet() }
		// TODO listen to Profile become events to strongly cache byCharacter
	}

	override fun forRace(race: Race): Collection<CharacterTrait> {
		return lock.readLock().withLock { byRace[race]!! }
	}

	override fun forCharacter(character: Character): Collection<CharacterTrait> {
		return lock.readLock().withLock { byCharacter[character] } ?: run {
			lock.writeLock().withLock {
				byCharacter[character] ?: run {
					val traits = deduplicate(child.forCharacter(character)).toHashSet()
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
			val traits = byCharacterDirty.computeIfAbsent(character) {
				byCharacter[character] ?: run {
					val traits = deduplicate(child.forCharacter(character)).toHashSet()
					byCharacter[character] = traits
					traits
				}
			}

			val oldTraits = traits.toSet()
			traits.add(trait)

			server.pluginManager.callEvent(CharacterChangeTraitsEvent(character, oldTraits, traits))
		}
	}

	override fun unlinkFromCharacter(character: Character, trait: CharacterTrait) {
		lock.writeLock().withLock {
			val traits = byCharacterDirty.computeIfAbsent(character) {
				byCharacter[character] ?: run {
					val traits = deduplicate(child.forCharacter(character)).toHashSet()
					byCharacter[character] = traits
					traits
				}
			}

			val oldTraits = traits.toSet()
			traits.remove(trait)
			server.pluginManager.callEvent(CharacterChangeTraitsEvent(character, oldTraits, traits))
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
