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
package com.fablesfantasyrp.plugin.staffprofiles.domain.repository

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.staffprofiles.dal.repository.StaffProfileDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StaffProfileRepositoryImpl(private val child: StaffProfileDataRepository,
								 private val profiles: ProfileRepository) : StaffProfileRepository {
	private val profileIds = HashSet<Int>()

	suspend fun init() {
		withContext(Dispatchers.IO) { child.all() }.forEach { profileIds.add(it) }
	}

	override suspend fun destroy(v: Profile) {
		val id = v.id
		withContext(Dispatchers.IO) { child.destroy(id) }
		profileIds.remove(v.id)
	}

	override suspend fun create(v: Profile): Profile {
		val id = v.id
		withContext(Dispatchers.IO) { child.create(id) }
		profileIds.add(id)
		return v
	}

	override suspend fun all(): Collection<Profile> = profiles.forIds(profileIds.asSequence())
	override suspend fun contains(v: Profile): Boolean = profileIds.contains(v.id)
	override suspend fun containsAny(v: Collection<Profile>): Boolean = v.any { profileIds.contains(it.id) }
	override suspend fun update(v: Profile) = throw UnsupportedOperationException()
	override suspend fun createOrUpdate(v: Profile): Profile = create(v)
}
