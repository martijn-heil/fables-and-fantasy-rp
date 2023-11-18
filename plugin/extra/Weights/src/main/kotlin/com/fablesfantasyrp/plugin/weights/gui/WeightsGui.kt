package com.fablesfantasyrp.plugin.weights.gui

import com.fablesfantasyrp.plugin.weights.WeightsConfig
import com.fablesfantasyrp.plugin.weights.calculateWeight
import com.fablesfantasyrp.plugin.weights.getSimpleWeight
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class WeightsGui(plugin: JavaPlugin, inventory: Inventory, weightsConfig: WeightsConfig)
	: InventoryGui(plugin, "Weights", arrayOf(
		"iiiiiiiii",
		"iiiiiiiii",
		"iiiiiiiii",
		"iiiiiiiii",
		"iiiiiiiii",
		"        t",
)) {
	init {
		val weight = calculateWeight(inventory.contents.filterNotNull(), weightsConfig)
		val elements = inventory.contents
				.map { it?.let { applyWeightLore(it, weightsConfig) } }
				.map { StaticGuiElement('e', it) }

		val group = GuiElementGroup('i')
		group.addElements(elements)

		this.addElements(group)
		this.addElement(StaticGuiElement('t', ItemStack(Material.WRITABLE_BOOK),
				"${ChatColor.GOLD}Total weight: " +
						"${if (weight > weightsConfig.cap) ChatColor.RED else ChatColor.GRAY}" +
						"$weight/${weightsConfig.cap} units"))
	}
}

private fun applyWeightLore(item: ItemStack, weightsConfig: WeightsConfig): ItemStack {
	val newItem = item.clone()
	val weight = getSimpleWeight(item, weightsConfig)
	val newLore = ArrayList<Component>()
	newItem.lore()?.let { newLore.addAll(it) }
	newLore.add(Component.text("${weight.weight} units").color(NamedTextColor.GRAY))
	if (weight.isSingular) {
		newLore.add(Component.text("This material is counted only once.").color(NamedTextColor.YELLOW))
	}
	newItem.lore(newLore)
	return newItem
}
