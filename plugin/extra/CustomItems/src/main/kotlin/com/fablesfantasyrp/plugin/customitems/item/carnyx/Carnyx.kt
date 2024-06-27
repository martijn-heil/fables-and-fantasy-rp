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
package com.fablesfantasyrp.plugin.customitems.item.carnyx

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.customModel
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.itemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.meta
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Carnyx {
	fun matches(item: ItemStack): Boolean {
		return item.type == Material.FERMENTED_SPIDER_EYE && item.itemMeta.customModel == 35
	}

	fun create(): ItemStack {
		return itemStack(Material.FERMENTED_SPIDER_EYE) {
			meta {
				customModel = 35
			}
		}
	}
}
