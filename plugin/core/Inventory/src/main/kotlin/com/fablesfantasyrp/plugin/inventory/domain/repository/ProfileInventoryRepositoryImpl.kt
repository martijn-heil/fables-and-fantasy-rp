package com.fablesfantasyrp.plugin.inventory.domain.repository

import com.fablesfantasyrp.plugin.database.async.repository.base.AsyncTypicalRepository
import com.fablesfantasyrp.plugin.inventory.domain.entity.ProfileInventory
import com.fablesfantasyrp.plugin.inventory.domain.mapper.ProfileInventoryMapper
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

class ProfileInventoryRepositoryImpl(child: ProfileInventoryMapper)
	: AsyncTypicalRepository<Int, ProfileInventory, ProfileInventoryMapper>(child), ProfileInventoryRepository {

	override suspend fun forOwner(owner: Profile): ProfileInventory
		= forId(owner.id) ?: deduplicate(child.forOwner(owner))
}
