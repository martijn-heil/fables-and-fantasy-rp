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
package com.fablesfantasyrp.plugin.magic.domain.repository

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Tear
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.distanceSafe
import org.bukkit.Location

class TearRepositoryImpl : SimpleMapRepository<Long, Tear>(), TearRepository, HasDirtyMarker<Tear> {
	override var dirtyMarker: DirtyMarker<Tear>? = null

	private var idCounter = 0L
	override fun create(v: Tear): Tear {
		v.dirtyMarker = dirtyMarker
		return super.create(Tear(
			id = idCounter++,
			location = v.location,
			magicType = v.magicType,
			owner = v.owner,
		))
	}

	override fun forOwner(owner: Character): Collection<Tear> {
		return this.all().filter { it.owner.id == owner.id }
	}

	override fun forLocation(location: Location): Tear? {
		return this.all().find { it.location.distanceSafe(location) < 0.1 }
	}

	override fun destroy(v: Tear) {
		super.destroy(v)
		v.isDeleted = true
		v.dirtyMarker = null
	}
}
