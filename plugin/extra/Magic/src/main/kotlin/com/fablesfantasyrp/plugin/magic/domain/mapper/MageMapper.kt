package com.fablesfantasyrp.plugin.magic.domain.mapper

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.database.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.magic.dal.model.MageData
import com.fablesfantasyrp.plugin.magic.dal.repository.MageDataRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository

class MageMapper(private val child: MageDataRepository)
	: MappingRepository<Long, MageData, Mage, MageDataRepository>(child),
	MageRepository, HasDirtyMarker<Mage> {

	override var dirtyMarker: DirtyMarker<Mage>? = null

	override fun convertFromChild(v: MageData) = Mage(
		id = v.id,
		magicLevel = v.magicLevel,
		magicPath = v.magicPath,
		spells = v.spells,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: Mage) = MageData(
		id = v.id,
		magicLevel = v.magicLevel,
		magicPath = v.magicPath,
		spells = v.spells
	)

	override fun forCharacter(c: Character): Mage? = child.forCharacter(c.id)?.let { convertFromChild(it) }
	override fun forCharacterOrCreate(c: Character): Mage = convertFromChild(child.forCharacterOrCreate(c.id))
}
