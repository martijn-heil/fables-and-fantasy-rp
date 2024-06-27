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
package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingNamedEntityRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.mapper.LodestoneMapper
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class LodestoneRepositoryImpl(child: LodestoneMapper) : MassivelyCachingNamedEntityRepository<Int, Lodestone, LodestoneMapper>(child), LodestoneRepository {
    private val byLocation = HashMap<BlockIdentifier, Lodestone>()

	override fun init() {
		super.init()
		strongCache.forEach { byLocation[it.location] = it }
	}

	override fun forLocation(location: BlockIdentifier): Lodestone? {
		return byLocation[location]
	}

	override fun create(v: Lodestone): Lodestone {
		val created = super.create(v)
		byLocation[created.location] = created
		return created
	}

	override fun forId(id: Int): Lodestone? {
		val found = super.forId(id)
		if (found != null) byLocation[found.location] = found
		return found
	}

	override fun destroy(v: Lodestone) {
		super.destroy(v)
		byLocation.remove(v.location)
		v.isDestroyed = true
	}

	override fun markDirty(v: Lodestone, what: String, oldValue: Any?, newValue: Any?) {
		super.markDirty(v, what, oldValue, newValue)

		if (what == "location") {
			if (oldValue != null) byLocation.remove(oldValue)
			if (newValue != null) byLocation[newValue as BlockIdentifier] = v
		}
	}
}
