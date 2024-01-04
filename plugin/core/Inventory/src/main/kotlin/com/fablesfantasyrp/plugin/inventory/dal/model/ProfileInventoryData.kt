package com.fablesfantasyrp.plugin.inventory.dal.model

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.inventory.PassthroughInventory
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory

data class ProfileInventoryData(
	val inventory: PassthroughPlayerInventory,
	val enderChest: PassthroughInventory,
	override val id: Int = 0,
) : Identifiable<Int>
