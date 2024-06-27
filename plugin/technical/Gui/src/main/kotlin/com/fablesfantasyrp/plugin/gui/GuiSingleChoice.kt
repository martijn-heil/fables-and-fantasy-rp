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

import de.themoep.inventorygui.GuiElement
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.GuiPageElement
import de.themoep.inventorygui.GuiPageElement.PageAction
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin


open class GuiSingleChoice<E>(plugin: JavaPlugin,
						 title: String,
						 values: Sequence<E>,
						 getItemStack: (value: E) -> ItemStack,
						 getText: ((value: E) -> String)? = null
)
	: ResultProducingInventoryGui<E>(plugin, title,
		arrayOf("ggggggggg",
				"ggggggggg",
				"ggggggggg",
				"ggggggggg",
				"ggggggggg",
				"fp     nl")) {

	init {
		val group = GuiElementGroup('g')

		for (value in values) {
			val itemStack = getItemStack(value)
			val text = if (getText != null) getText(value) else null

			val action: (GuiElement.Click) -> Boolean = {
				this.result.complete(value)
				true
			}

			if (text != null) {
				group.addElement(StaticGuiElement('e', itemStack, action, text))
			} else {
				group.addElement(StaticGuiElement('e', itemStack, action))
			}
		}

		this.addElement(group)
		this.addElement(GuiPageElement('f', ItemStack(Material.ARROW), PageAction.FIRST, "Go to first page (current: %page%)"))
		this.addElement(GuiPageElement('p', ItemStack(Material.OAK_SIGN), PageAction.PREVIOUS, "Go to previous page (%prevpage%)"))
		this.addElement(GuiPageElement('n', ItemStack(Material.OAK_SIGN), PageAction.NEXT, "Go to next page (%nextpage%)"))
		this.addElement(GuiPageElement('l', ItemStack(Material.ARROW), PageAction.LAST, "Go to last page (%pages%)"))
	}
}
