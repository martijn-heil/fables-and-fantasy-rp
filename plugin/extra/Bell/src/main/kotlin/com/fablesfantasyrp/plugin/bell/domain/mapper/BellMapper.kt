/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
