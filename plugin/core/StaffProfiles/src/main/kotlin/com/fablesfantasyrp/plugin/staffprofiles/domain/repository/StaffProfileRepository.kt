package com.fablesfantasyrp.plugin.staffprofiles.domain.repository

import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.database.async.repository.AsyncRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

interface StaffProfileRepository : AsyncRepository<Profile>, AsyncMutableRepository<Profile> {
}
