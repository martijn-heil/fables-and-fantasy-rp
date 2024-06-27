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
package com.fablesfantasyrp.plugin.gui.element

import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


private val FILLED_ITEM = ItemStack(Material.GREEN_STAINED_GLASS_PANE)
private val EMPTY_ITEM = ItemStack(Material.BLACK_STAINED_GLASS_PANE)

class Slider(char: Char,
			 max: UInt,
			 initial: UInt,
			 valueToDisplayValue: (UInt) -> UInt,
			 canIncrease: (UInt) -> Boolean) : GuiElementGroup(char) {
	var value = initial

	init {
		require(max <= 9U)
		require(initial <= max)
		this.slots

		(1U .. max).forEach { sectionValue ->
			when (sectionValue) {
				1U -> {
					this.addElement(DynamicGuiElement('e') { ->
						val displayValue = valueToDisplayValue(value)
						val isFilled = value >= sectionValue
						val item = if (isFilled) FILLED_ITEM else EMPTY_ITEM


						StaticGuiElement('e', item, displayValue.toInt(), {
							if (value > 0U) value--
							gui.draw()
							true
						}, "${ChatColor.RED}${ChatColor.BOLD}-")
					})
				}

				max -> {
					this.addElement(DynamicGuiElement('e') { ->
						val isFilled = value >= sectionValue
						val item = if (isFilled) FILLED_ITEM else EMPTY_ITEM

						StaticGuiElement('e', item, 1, {
							if (value < max && canIncrease(value)) value++
							gui.draw()
							true
						}, "${ChatColor.GREEN}${ChatColor.BOLD}+")
					})
				}

				else -> {
					this.addElement(DynamicGuiElement('e') { ->
						val isFilled = value >= sectionValue
						val item = if (isFilled) FILLED_ITEM else EMPTY_ITEM

						StaticGuiElement('e', item, 1, { true }, " ")
					})
				}
			}
		}
	}
}
