package com.fablesfantasyrp.plugin.magic.domain.repository

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.mapper.MageMapper

class MageRepositoryImpl(child: MageMapper) : MassivelyCachingEntityRepository<Long, Mage, MageMapper>(child), MageRepository {

	private fun forIdOrCreate(id: Long): Mage {
		val maybe = this.forId(id)
		return if (maybe != null) {
			maybe
		} else {
			val obj = Mage(
					id = id,
					magicPath = MagicPath.AEROMANCY,
					magicLevel = 0,
					spells = emptyList()
			)
			val result = this.create(obj)
			result.dirtyMarker = this
			result
		}
	}

	override fun forCharacter(c: Character): Mage? = this.forId(c.id.toLong())
	override fun forCharacterOrCreate(c: Character): Mage = forIdOrCreate(c.id.toLong())

	override fun destroy(v: Mage) {
		super.destroy(v)
		v.isDeleted = true
	}
}
