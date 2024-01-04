package com.fablesfantasyrp.plugin.inventory.domain

import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

interface FablesPlayerInventory : FablesInventory {
	var heldItemSlot: Int
	val armorContents: List<ItemStack?>
	var boots: ItemStack?
	var leggings: ItemStack?
	var chestplate: ItemStack?
	var helmet: ItemStack?
	var itemInMainHand: ItemStack?
	var itemInOffHand: ItemStack?
	operator fun set(equipmentSlot: EquipmentSlot, value: ItemStack?)
}
