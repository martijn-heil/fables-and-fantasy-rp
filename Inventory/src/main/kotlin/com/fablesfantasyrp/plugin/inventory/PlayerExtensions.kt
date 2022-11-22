package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.inventory.data.entity.PlayerInstanceInventory
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

val PlayerInstance.inventory: PlayerInstanceInventory get() = PLUGIN.inventories.forOwner(this)
