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
package com.fablesfantasyrp.plugin.gui

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.customModel
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.itemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.meta
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.name
import net.kyori.adventure.text.Component
import org.bukkit.Material

object Icon {
	val CHECKMARK = itemStack(Material.EMERALD) 		{ meta { customModel = 1; name = Component.empty() } }
	val X = itemStack(Material.REDSTONE) 				{ meta { customModel = 1; name = Component.empty() } }
	val TRASH_BIN = itemStack(Material.HOPPER_MINECART) { meta { customModel = 1; name = Component.empty() } }
	val UP = itemStack(Material.MAP) 					{ meta { customModel = 2; name = Component.empty() } }
	val DOWN = itemStack(Material.FILLED_MAP) 			{ meta { customModel = 2; name = Component.empty() } }
	val INFO = itemStack(Material.SUNFLOWER)			{ meta { customModel = 1; name = Component.empty() }}

	val ANDROS = itemStack(Material.PAPER) 				{}

	fun digit(digit: Int) = itemStack(Material.PLAYER_HEAD) { meta { customModel = digit } }
}
