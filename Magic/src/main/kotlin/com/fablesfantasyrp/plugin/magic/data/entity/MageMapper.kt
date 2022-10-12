package com.fablesfantasyrp.plugin.magic.data.entity

import com.fablesfantasyrp.plugin.database.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.magic.data.SimpleMageData
import com.fablesfantasyrp.plugin.magic.data.SimpleMageDataRepository

class MageMapper(private val child: SimpleMageDataRepository)
	: MappingRepository<Long, SimpleMageData, Mage,
		SimpleMageDataRepository>(child),
		HasDirtyMarker<Mage> {
	override var dirtyMarker: DirtyMarker<Mage>? = null

	override fun forId(id: Long): Mage? = child.forId(id)?.let { convertFromChild(it) }

	override fun all(): Collection<Mage> = child.all().map { convertFromChild(it) }

	override fun convertFromChild(v: SimpleMageData): Mage {
		val obj = Mage(
				id = v.id,
				magicPath = v.magicPath,
				magicLevel = v.magicLevel,
				spells = v.spells
		)
		obj.dirtyMarker = dirtyMarker
		return obj
	}

	override fun convertToChild(v: Mage): SimpleMageData = SimpleMageData(
			id = v.id,
			magicLevel = v.magicLevel,
			magicPath = v.magicPath,
			spells = v.spells
	)
}
