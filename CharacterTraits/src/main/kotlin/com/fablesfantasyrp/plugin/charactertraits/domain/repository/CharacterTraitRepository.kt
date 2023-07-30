package com.fablesfantasyrp.plugin.charactertraits.domain.repository

import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.charactertraits.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface CharacterTraitRepository : MutableRepository<CharacterTrait>, KeyedRepository<String, CharacterTrait> {
	fun forRace(race: Race): Collection<CharacterTrait>
	fun forCharacter(character: Character): Collection<CharacterTrait>
	fun linkToRace(race: Race, trait: CharacterTrait)
	fun unlinkFromRace(race: Race, trait: CharacterTrait)
	fun linkToCharacter(character: Character, trait: CharacterTrait)
	fun unlinkFromCharacter(character: Character, trait: CharacterTrait)
}
