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
