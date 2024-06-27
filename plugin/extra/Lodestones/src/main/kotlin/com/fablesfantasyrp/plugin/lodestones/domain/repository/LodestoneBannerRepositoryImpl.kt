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

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.lodestones.domain.mapper.LodestoneBannerMapper
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.distanceSafe
import org.bukkit.Location

class LodestoneBannerRepositoryImpl(child: LodestoneBannerMapper, private val lodestones: LodestoneRepository)
	: MassivelyCachingEntityRepository<Int, LodestoneBanner, LodestoneBannerMapper>(child), LodestoneBannerRepository {
    private val byLocation = HashMap<BlockIdentifier, LodestoneBanner>()
	private val byLodestone = HashMap<Int, LodestoneBanner>()

	override fun init() {
		super.init()
		strongCache.forEach {
			byLocation[it.location] = it
			byLodestone[it.lodestone.id] = it
		}
		lodestones.onDestroy { byLodestone[it.id]?.let { banner -> this.destroy(banner) } }
	}

	override fun forLocation(location: BlockIdentifier): LodestoneBanner? {
		return byLocation[location]
	}

	override fun near(location: Location): LodestoneBanner? {
		return all()
			.asSequence()
			.map { Pair(it, it.location.toLocation().distanceSafe(location)) }
			.filter { it.second < 3 }
			.sortedBy { it.second }
			.firstOrNull()?.first
	}

	override fun create(v: LodestoneBanner): LodestoneBanner {
		val created = super.create(v)
		byLocation[created.location] = created
		byLodestone[created.lodestone.id] = created
		return created
	}

	override fun forId(id: Int): LodestoneBanner? {
		val found = super.forId(id)
		if (found != null) {
			byLocation[found.location] = found
			byLodestone[found.lodestone.id] = found
		}
		return found
	}

	override fun destroy(v: LodestoneBanner) {
		super.destroy(v)
		byLocation.remove(v.location)
		byLodestone.remove(v.lodestone.id)
		v.isDestroyed = true
	}

	override fun markDirty(v: LodestoneBanner, what: String, oldValue: Any?, newValue: Any?) {
		super.markDirty(v, what, oldValue, newValue)

		if (what == "location") {
			if (oldValue != null) byLocation.remove(oldValue)
			if (newValue != null) byLocation[newValue as BlockIdentifier] = v
		}
	}
}
