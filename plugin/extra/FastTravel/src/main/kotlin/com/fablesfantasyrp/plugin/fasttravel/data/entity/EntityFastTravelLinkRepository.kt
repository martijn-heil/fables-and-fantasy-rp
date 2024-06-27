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

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion

class EntityFastTravelLinkRepository<C>(child: C) : MassivelyCachingEntityRepository<Int, FastTravelLink, C>(child), FastTravelLinkRepository
		where C: KeyedRepository<Int, FastTravelLink>,
			  C: MutableRepository<FastTravelLink>,
			  C: HasDirtyMarker<FastTravelLink>,
			  C: FastTravelLinkRepository {
	private val byFromRegion = HashMap<WorldGuardRegion, FastTravelLink>()

	override fun init() {
		super.init()
		all().forEach { byFromRegion[it.from] = it }
	}

	override fun create(v: FastTravelLink): FastTravelLink {
		val created = super.create(v)
		byFromRegion[created.from] = created
		return created
	}

	override fun destroy(v: FastTravelLink) {
		super.destroy(v)
		byFromRegion.remove(v.from)
	}

	override fun forOriginRegion(region: WorldGuardRegion): FastTravelLink? {
		return byFromRegion[region]
	}
}
