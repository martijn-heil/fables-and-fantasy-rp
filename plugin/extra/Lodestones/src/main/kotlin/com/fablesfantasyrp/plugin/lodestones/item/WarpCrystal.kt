/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
