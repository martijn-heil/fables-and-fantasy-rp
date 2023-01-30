package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.inventory.data.entity.ProfileInventory
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

val Profile.inventory: ProfileInventory get() = PLUGIN.inventories.forOwner(this)
