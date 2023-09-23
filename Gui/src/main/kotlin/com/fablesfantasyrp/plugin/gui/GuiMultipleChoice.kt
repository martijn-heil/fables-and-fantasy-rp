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
