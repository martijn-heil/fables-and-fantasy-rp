package com.fablesfantasyrp.plugin.shops.dal.model

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.inventory.ItemStack
import java.time.Instant

data class ShopData(
	val location: BlockIdentifier,
	val owner: Int?,
	val item: ItemStack,
	val lastActive: Instant,
	val amount: Int,
	val buyPrice: Int,
	val sellPrice: Int,
	val stock: Int,
	override val id: Int = 0) : Identifiable<Int>
