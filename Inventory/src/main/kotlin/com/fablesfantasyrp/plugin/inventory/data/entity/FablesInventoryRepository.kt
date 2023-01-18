package com.fablesfantasyrp.plugin.inventory.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

interface FablesInventoryRepository :
		Repository<ProfileInventory>,
		MutableRepository<ProfileInventory>,
		KeyedRepository<Int, ProfileInventory> {
			fun forOwner(profile: Profile): ProfileInventory
}
