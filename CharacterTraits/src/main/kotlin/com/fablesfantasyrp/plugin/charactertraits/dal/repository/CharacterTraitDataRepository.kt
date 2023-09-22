package com.fablesfantasyrp.plugin.charactertraits.dal.repository

import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.charactertraits.dal.model.CharacterTraitData
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface CharacterTraitDataRepository : MutableRepository<CharacterTraitData>, KeyedRepository<String, CharacterTraitData> {
	fun forRace(race: Race): Collection<CharacterTraitData>
	fun forCharacter(character: Character): Collection<CharacterTraitData>
	fun linkToRace(race: Race, trait: CharacterTraitData)
	fun unlinkFromRace(race: Race, trait: CharacterTraitData)
	fun linkToCharacter(characterId: Int, trait: CharacterTraitData)
	fun unlinkFromCharacter(characterId: Int, trait: CharacterTraitData)
}
