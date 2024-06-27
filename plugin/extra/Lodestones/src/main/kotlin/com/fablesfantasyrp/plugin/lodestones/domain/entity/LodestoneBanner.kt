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
package com.fablesfantasyrp.plugin.lodestones.domain.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class LodestoneBanner : DataEntity<Int, LodestoneBanner> {
	override var dirtyMarker: DirtyMarker<LodestoneBanner>? = null
	override val id: Int

	var isDestroyed: Boolean = false

	var location: BlockIdentifier	set(value) { if (field != value) { val oldValue = field; field = value; dirtyMarker?.markDirty(this, "location", oldValue, value) } }
	val lodestone: Lodestone

	constructor(id: Int,
				location: BlockIdentifier,
				lodestone: Lodestone,
				dirtyMarker: DirtyMarker<LodestoneBanner>? = null) {
		this.id = id
		this.location = location
		this.lodestone = lodestone

		this.dirtyMarker = dirtyMarker // Must be last
	}
}
