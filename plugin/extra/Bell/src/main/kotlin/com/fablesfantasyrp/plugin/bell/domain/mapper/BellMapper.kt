package com.fablesfantasyrp.plugin.bell.domain.mapper

import com.fablesfantasyrp.plugin.bell.dal.model.BellData
import com.fablesfantasyrp.plugin.bell.dal.repository.BellDataRepository
import com.fablesfantasyrp.plugin.bell.domain.entity.Bell
import com.fablesfantasyrp.plugin.bell.domain.repository.BellRepository
import com.fablesfantasyrp.plugin.database.sync.repository.base.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class BellMapper(private val child: BellDataRepository)
	: MappingRepository<Int, BellData, Bell, BellDataRepository>(child), BellRepository, HasDirtyMarker<Bell> {

	override var dirtyMarker: DirtyMarker<Bell>? = null

	override fun convertFromChild(v: BellData) = Bell(
		id = v.id,
		location = v.location,
		locationName = v.name,
		discordChannelId = v.discordChannelId,
		discordRoleIds = v.discordRoleIds,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: Bell) = BellData(
		id = v.id,
		name = v.name,
		location = v.location,
		discordChannelId = v.discordChannelId,
		discordRoleIds = v.discordRoleIds,
	)

	override fun forLocation(location: BlockIdentifier): Bell? { throw NotImplementedError() }
	override fun forName(name: String): Bell? = child.forName(name)?.let { convertFromChild(it) }
	override fun nameExists(name: String): Boolean = child.nameExists(name)
	override fun allNames(): Set<String> = child.allNames()
}
