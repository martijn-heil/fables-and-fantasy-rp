package com.fablesfantasyrp.plugin.inventory.domain.repository

import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.inventory.domain.entity.ProfileInventory
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

interface ProfileInventoryRepository : AsyncMutableRepository<ProfileInventory>, AsyncKeyedRepository<Int, ProfileInventory> {
	suspend fun forOwner(owner: Profile): ProfileInventory
}
