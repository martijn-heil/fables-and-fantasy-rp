package com.fablesfantasyrp.plugin.magic.data.entity

import com.fablesfantasyrp.plugin.database.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.magic.data.SimpleTearData
import com.fablesfantasyrp.plugin.magic.data.SimpleTearDataRepository

class TearMapper(private val child: SimpleTearDataRepository)
	: MappingRepository<Long, SimpleTearData, Tear,
		SimpleTearDataRepository>(child),
		HasDirtyMarker<Tear> {
	override var dirtyMarker: DirtyMarker<Tear>? = null

	override fun forId(id: Long): Tear? = child.forId(id)?.let { convertFromChild(it) }

	override fun all(): Collection<Tear> = child.all().map { convertFromChild(it) }

	override fun convertFromChild(v: SimpleTearData): Tear {
		val obj = Tear(
				id = v.id,
				magicType = v.magicType,
				location = v.location,
				owner = v.owner,
		)
		obj.dirtyMarker = dirtyMarker
		return obj
	}

	override fun convertToChild(v: Tear) = SimpleTearData(
			id = v.id,
			magicType = v.magicType,
			location = v.location,
			owner = v.owner
	)
}
