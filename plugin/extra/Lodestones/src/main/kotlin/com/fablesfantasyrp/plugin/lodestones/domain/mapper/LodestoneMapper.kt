package com.fablesfantasyrp.plugin.lodestones.domain.mapper

import com.fablesfantasyrp.plugin.database.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.lodestones.dal.model.LodestoneData
import com.fablesfantasyrp.plugin.lodestones.dal.repository.LodestoneDataRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class LodestoneMapper(private val child: LodestoneDataRepository)
	: MappingRepository<Int, LodestoneData, Lodestone, LodestoneDataRepository>(child), LodestoneRepository, HasDirtyMarker<Lodestone> {

	override var dirtyMarker: DirtyMarker<Lodestone>? = null

	override fun convertFromChild(v: LodestoneData) = Lodestone(
		id = v.id,
		location = v.location,
		name = v.name,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: Lodestone) = LodestoneData(
		id = v.id,
		name = v.name,
		location = v.location,
	)

	override fun forLocation(location: BlockIdentifier): Lodestone? { throw NotImplementedError() }
	override fun forName(name: String): Lodestone? = child.forName(name)?.let { convertFromChild(it) }
	override fun nameExists(name: String): Boolean = child.nameExists(name)
	override fun allNames(): Set<String> = child.allNames()
}
