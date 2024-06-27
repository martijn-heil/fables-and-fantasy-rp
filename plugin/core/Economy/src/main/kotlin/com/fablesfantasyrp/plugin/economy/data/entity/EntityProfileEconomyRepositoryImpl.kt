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
package com.fablesfantasyrp.plugin.economy.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import java.lang.ref.SoftReference
import kotlin.concurrent.withLock

class EntityProfileEconomyRepositoryImpl<C>(child: C)
	: MassivelyCachingEntityRepository<Int, ProfileEconomy, C>(child),
		ProfileEconomyRepository,
		EntityProfileEconomyRepository
		where C: KeyedRepository<Int, ProfileEconomy>,
			  C: MutableRepository<ProfileEconomy>,
			  C: HasDirtyMarker<ProfileEconomy>,
			  C: ProfileEconomyRepository {
	override fun forProfile(profile: Profile): ProfileEconomy {
		return this.forId(profile.id) ?: run {
			val result = child.forProfile(profile)
			lock.writeLock().withLock {
				cache[result.id] = SoftReference(result)
			}
			this.markStrong(result)
			result
		}
	}
}
