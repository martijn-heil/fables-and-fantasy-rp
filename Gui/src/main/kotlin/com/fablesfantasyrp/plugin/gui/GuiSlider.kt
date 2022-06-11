package com.fablesfantasyrp.plugin.gui

import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

val FILLED_ITEM = ItemStack(Material.GREEN_STAINED_GLASS_PANE)
val EMPTY_ITEM = ItemStack(Material.BLACK_STAINED_GLASS_PANE)

class GuiSlider(char: Char,
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
						}, "$RED$BOLD-")
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
						}, "$GREEN$BOLD+")
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
