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
package com.fablesfantasyrp.plugin.fasttravel.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.fasttravel.data.FastTravelLinkData
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import org.bukkit.Location
import kotlin.time.Duration

class FastTravelLink : DataEntity<Int, FastTravelLink>, FastTravelLinkData {
	override var dirtyMarker: DirtyMarker<FastTravelLink>? = null
	var isDestroyed = false

	override val from: WorldGuardRegion
	override val to: Location
	override val travelDuration: Duration
	override val id: Int

	constructor(id: Int,
				from: WorldGuardRegion,
				to: Location,
				travelDuration: Duration,
				dirtyMarker: DirtyMarker<FastTravelLink>? = null) {
		this.id = id
		this.from = from
		this.to = to
		this.travelDuration = travelDuration

		this.dirtyMarker = dirtyMarker
	}
}
