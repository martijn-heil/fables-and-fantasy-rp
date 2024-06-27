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
package com.fablesfantasyrp.plugin.lodestones.domain.mapper

import com.fablesfantasyrp.plugin.database.sync.repository.base.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
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
		isPublic = v.isPublic,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: Lodestone) = LodestoneData(
		id = v.id,
		name = v.name,
		isPublic = v.isPublic,
		location = v.location,
	)

	override fun forLocation(location: BlockIdentifier): Lodestone? { throw NotImplementedError() }
	override fun forName(name: String): Lodestone? = child.forName(name)?.let { convertFromChild(it) }
	override fun nameExists(name: String): Boolean = child.nameExists(name)
	override fun allNames(): Set<String> = child.allNames()
}
