package com.fablesfantasyrp.plugin.inventory.domain.mapper

import com.fablesfantasyrp.plugin.database.CacheMarker
import com.fablesfantasyrp.plugin.database.async.repository.base.AsyncMappingRepository
import com.fablesfantasyrp.plugin.database.model.HasCacheMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.inventory.SerializableInventory
import com.fablesfantasyrp.plugin.inventory.dal.model.ProfileInventoryData
import com.fablesfantasyrp.plugin.inventory.dal.repository.ProfileInventoryDataRepository
import com.fablesfantasyrp.plugin.inventory.domain.entity.ProfileInventory
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileInventoryMapper(child: ProfileInventoryDataRepository)
	: AsyncMappingRepository<Int, ProfileInventoryData, ProfileInventory, ProfileInventoryDataRepository>(child),
	HasDirtyMarker<ProfileInventory>, HasCacheMarker<ProfileInventory> {

	override var dirtyMarker: DirtyMarker<ProfileInventory>? = null
	override var cacheMarker: CacheMarker<ProfileInventory>? = null

	override fun convertFromChild(v: ProfileInventoryData): ProfileInventory {
		val entity = ProfileInventory(
			id = v.id,
			inventory = v.inventory.clone(),
			enderChest = v.enderChest.clone(),
			dirtyMarker = dirtyMarker,
		)

		if (dirtyMarker != null) {
			val inventoryDirtyMarker = InventoryDirtyMarker(entity, dirtyMarker!!)
			val inventoryCacheMarker = InventoryCacheMarker(entity, cacheMarker!!)
			entity.inventory.dirtyMarker = inventoryDirtyMarker
			entity.inventory.cacheMarker = inventoryCacheMarker

			entity.enderChest.dirtyMarker = inventoryDirtyMarker
			entity.enderChest.cacheMarker = inventoryCacheMarker
		}

		return entity
	}


	override fun convertToChild(v: ProfileInventory): ProfileInventoryData = ProfileInventoryData(
		id = v.id,
		inventory = v.inventory.clone(),
		enderChest = v.enderChest.clone()
	)

	suspend fun forOwner(owner: Profile)
		= withContext(Dispatchers.IO) { child.forOwner(owner.id) }
			.let { convertFromChild(it) }

	private class InventoryDirtyMarker(
		private val profileInventory: ProfileInventory,
		private val dirtyMarker: DirtyMarker<ProfileInventory>) : DirtyMarker<SerializableInventory> {
		override fun markDirty(v: SerializableInventory) = dirtyMarker.markDirty(profileInventory)
	}

	private class InventoryCacheMarker(
		private val profileInventory: ProfileInventory,
		private val cacheMarker: CacheMarker<ProfileInventory>) : CacheMarker<SerializableInventory> {
		override fun markStrong(v: SerializableInventory) = cacheMarker.markStrong(profileInventory)
		override fun markWeak(v: SerializableInventory) = cacheMarker.markWeak(profileInventory)
	}
}
