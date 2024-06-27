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
import org.bukkit.Material.BLACK_STAINED_GLASS_PANE
import org.bukkit.Material.GREEN_STAINED_GLASS_PANE
import org.bukkit.inventory.ItemStack

class MultipleChoice<E>(
	char: Char,
	values: Collection<E>,
	val maxSelectionCount: Int,
	render: (E) -> String) : GuiElementGroup(char) {
	val selected: MutableSet<E> = HashSet()

	init {
		this.addElements(values.map { value ->
				DynamicGuiElement(char) { _ ->
					val isSelected = selected.contains(value)
					val material = if (isSelected) GREEN_STAINED_GLASS_PANE else BLACK_STAINED_GLASS_PANE
					val actionText = if (isSelected) {
						"\n${ChatColor.DARK_PURPLE}${ChatColor.ITALIC}" +
							"This item is currently selected!\n" +
							"Click again to deselect."
					} else if (selected.size >= maxSelectionCount) {
						"\n${ChatColor.RED}${ChatColor.ITALIC}" +
							"You can only select $maxSelectionCount items,\n" +
							"please deselect another item first."
					} else {
						"\n${ChatColor.DARK_PURPLE}${ChatColor.ITALIC}Click to select."
					}
					StaticGuiElement(char, ItemStack(material), {
						if (isSelected) {
							selected.remove(value)
							gui.draw()
						} else if (selected.size < maxSelectionCount) {
							selected.add(value)
							gui.draw()
						}
						true
					}, render(value) + actionText)
				}
			}
		)
	}
}
