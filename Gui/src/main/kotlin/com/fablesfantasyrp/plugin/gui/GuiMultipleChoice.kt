package com.fablesfantasyrp.plugin.gui

import com.fablesfantasyrp.plugin.gui.element.MultipleChoice
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class GuiMultipleChoice<T>(plugin: JavaPlugin,
						   title: String,
						   maxSelectionCount: Int,
						   values: Set<T>,
						   render: (T) -> String)
	: ResultProducingInventoryGui<Set<T>>(plugin, title,
	arrayOf(
		"ggggggggg",
		"xxxxvxxxx")) {

	private val multipleChoice = MultipleChoice('g', values, maxSelectionCount, render)

	init {
		this.addElement(multipleChoice)
		this.addElement(StaticGuiElement('v', Icon.CHECKMARK, {
			result.complete(multipleChoice.selected)
			true
		}, "${ChatColor.GREEN}Confirm"))
	}
}
