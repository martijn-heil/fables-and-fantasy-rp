package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.inventory.data.entity.FablesInventoryRepository
import com.fablesfantasyrp.plugin.inventory.data.entity.ProfileInventory
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.koin.core.context.GlobalContext

val Profile.inventory: ProfileInventory get() = GlobalContext.get().get<FablesInventoryRepository>().forOwner(this)
