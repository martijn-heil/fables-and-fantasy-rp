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
import com.fablesfantasyrp.plugin.lodestones.dal.model.LodestoneBannerData
import com.fablesfantasyrp.plugin.lodestones.dal.repository.LodestoneBannerDataRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.Location

class LodestoneBannerMapper(private val child: LodestoneBannerDataRepository, private val lodestones: LodestoneRepository)
	: MappingRepository<Int, LodestoneBannerData, LodestoneBanner, LodestoneBannerDataRepository>(child), LodestoneBannerRepository, HasDirtyMarker<LodestoneBanner> {

	override var dirtyMarker: DirtyMarker<LodestoneBanner>? = null

	override fun convertFromChild(v: LodestoneBannerData) = LodestoneBanner(
		id = v.id,
		location = v.location,
		lodestone = lodestones.forId(v.lodestoneId)!!,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: LodestoneBanner) = LodestoneBannerData(
		id = v.id,
		location = v.location,
		lodestoneId = v.lodestone.id
	)

	override fun forLocation(location: BlockIdentifier): LodestoneBanner? { throw NotImplementedError() }
	override fun near(location: Location): LodestoneBanner? { throw NotImplementedError() }
}
