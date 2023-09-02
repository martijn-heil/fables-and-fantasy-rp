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
						"\n${ChatColor.GRAY}${ChatColor.ITALIC}" +
							"This item is currently selected!\n" +
							"Click again to deselect."
					} else if (selected.size >= maxSelectionCount) {
						"\n${ChatColor.RED}${ChatColor.ITALIC}" +
							"You can only select $maxSelectionCount items,\n" +
							"please deselect another item first."
					} else {
						"\n${ChatColor.GRAY}${ChatColor.ITALIC}Click to select."
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
