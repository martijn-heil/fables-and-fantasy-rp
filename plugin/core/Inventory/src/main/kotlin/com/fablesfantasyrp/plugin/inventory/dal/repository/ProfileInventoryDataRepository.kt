package com.fablesfantasyrp.plugin.inventory.dal.repository

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.inventory.dal.model.ProfileInventoryData

interface ProfileInventoryDataRepository : MutableRepository<ProfileInventoryData>, KeyedRepository<Int, ProfileInventoryData> {
	fun forOwner(profileId: Int): ProfileInventoryData
}
