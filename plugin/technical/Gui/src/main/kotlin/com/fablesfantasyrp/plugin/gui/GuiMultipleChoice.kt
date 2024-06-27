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

import com.fablesfantasyrp.plugin.gui.element.MultipleChoice
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class GuiMultipleChoice<T>(plugin: JavaPlugin,
						   title: String,
						   maxSelectionCount: Int,
						   minSelectionCount: Int,
						   values: Set<T>,
						   render: (T) -> String)
	: ResultProducingInventoryGui<Set<T>>(plugin, title,
	arrayOf(
		"ggggggggg",
		"xxxxvxxxx")) {

	private val multipleChoice = MultipleChoice('g', values, maxSelectionCount, render)

	init {
		assert(maxSelectionCount >= minSelectionCount)

		this.addElement(multipleChoice)

		this.addElement(DynamicGuiElement('v') { _ ->
			val text = if (multipleChoice.selected.size < minSelectionCount) {
				"${ChatColor.GRAY}Confirm\n" +
					if (maxSelectionCount == minSelectionCount) {
						"${ChatColor.RED}${ChatColor.ITALIC}Please select $minSelectionCount items."
					} else {
						"${ChatColor.RED}${ChatColor.ITALIC}Please select between $minSelectionCount and $maxSelectionCount items."
					}
			} else {
				"${ChatColor.GREEN}Confirm"
			}

			StaticGuiElement('v', Icon.CHECKMARK, {
				if (multipleChoice.selected.size >= minSelectionCount) {
					result.complete(multipleChoice.selected)
				}
				true
			}, text)
		})
	}
}
