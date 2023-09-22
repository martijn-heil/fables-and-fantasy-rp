package com.fablesfantasyrp.plugin.charactertraits.domain.mapper

import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.charactertraits.dal.model.CharacterTraitData
import com.fablesfantasyrp.plugin.charactertraits.dal.repository.CharacterTraitDataRepository
import com.fablesfantasyrp.plugin.charactertraits.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.database.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker

class CharacterTraitMapper(private val child: CharacterTraitDataRepository)
	: MappingRepository<String, CharacterTraitData, CharacterTrait, CharacterTraitDataRepository>(child),
	CharacterTraitRepository, HasDirtyMarker<CharacterTrait> {

	override var dirtyMarker: DirtyMarker<CharacterTrait>? = null

	override fun convertFromChild(v: CharacterTraitData) = CharacterTrait(v.id, v.displayName, v.description, dirtyMarker)
	override fun convertToChild(v: CharacterTrait) = CharacterTraitData(v.id, v.displayName, v.description)
	override fun forRace(race: Race) = child.forRace(race).map { convertFromChild(it) }
	override fun forCharacter(character: Character) = child.forCharacter(character).map { convertFromChild(it) }
	override fun linkToRace(race: Race, trait: CharacterTrait) = child.linkToRace(race, convertToChild(trait))
	override fun unlinkFromRace(race: Race, trait: CharacterTrait) = child.unlinkFromRace(race, convertToChild(trait))
	override fun linkToCharacter(character: Character, trait: CharacterTrait) = child.linkToCharacter(character.id, convertToChild(trait))
	override fun unlinkFromCharacter(character: Character, trait: CharacterTrait) = child.unlinkFromCharacter(character.id, convertToChild(trait))
}
