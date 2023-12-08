package com.fablesfantasyrp.plugin.location.data.entity

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.Repository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

interface ProfileLocationRepository :
		Repository<ProfileLocation>,
	MutableRepository<ProfileLocation>,
	KeyedRepository<Int, ProfileLocation> {
			fun forOwner(profile: Profile): ProfileLocation
}
