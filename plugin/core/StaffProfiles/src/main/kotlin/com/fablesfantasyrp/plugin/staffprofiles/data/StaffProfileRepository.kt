package com.fablesfantasyrp.plugin.staffprofiles.data

import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

interface StaffProfileRepository : MutableRepository<Profile> {
	override fun contains(v: Profile): Boolean
}
