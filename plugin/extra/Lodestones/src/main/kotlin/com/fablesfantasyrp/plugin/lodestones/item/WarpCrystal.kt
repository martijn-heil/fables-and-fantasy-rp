package com.fablesfantasyrp.plugin.lodestones.item

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object WarpCrystal {
	fun matches(item: ItemStack): Boolean {
		return item.type == Material.NETHER_STAR && !isArmorStandToolsTool(item)
	}

	fun create(): ItemStack {
		return ItemStack(Material.NETHER_STAR)
	}

	/**
	 * Check whether this nether star is the astools tool
	 * astools is a plugin that lets you modify armor stands using a nice interface.
	 * It makes you enter it's astools mode and takes control over your inventory, putting a nether star
	 * among other things in it to let you control aspects of the subject armorstand.
	 * We don't want to match the astools nether star when checking whether a nether star is a warp crystal.
	 */
	private fun isArmorStandToolsTool(item: ItemStack): Boolean {
		val serializer = PlainTextComponentSerializer.plainText()
		val displayName = item.itemMeta.displayName() ?: return false
		val name = serializer.serialize(displayName).trim()
		return name == "GUI Multi-Tool"
	}
}
