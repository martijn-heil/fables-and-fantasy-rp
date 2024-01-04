package com.fablesfantasyrp.plugin.inventorysearch.gui

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.shortName
import com.fablesfantasyrp.plugin.economy.formatMoney
import com.fablesfantasyrp.plugin.economy.money
import com.fablesfantasyrp.plugin.inventory.domain.FablesInventory
import com.fablesfantasyrp.plugin.item.ItemTrait
import com.fablesfantasyrp.plugin.item.ItemTraitService
import de.themoep.inventorygui.GuiElementGroup
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class InventorySearchGui(plugin: JavaPlugin, target: Character,
						 private val targetInventory: FablesInventory,
						 private val itemTraitService: ItemTraitService)
	: InventoryGui(plugin, "Searching ${target.shortName}", arrayOf(
	"ggggggggg",
	"ggggggggg",
	"ggggggggg",
	"ggggggggg",
	"ggggggggm")) {
	init {
		val group = GuiElementGroup('g')
		targetInventory.contents
			.map { if (it != null && canSee(it)) it else ItemStack(Material.AIR) }
			.map { StaticGuiElement('g', it) }
			.forEach { group.addElement(it) }

		this.addElement(group)

		val money = target.profile.money
		this.addElement(StaticGuiElement('m',
			ItemStack(Material.YELLOW_STAINED_GLASS_PANE), "${ChatColor.YELLOW}${formatMoney(money)}"))
	}

	private fun canSee(item: ItemStack): Boolean = !itemTraitService.getTraits(item).contains(ItemTrait.HIDDEN.key)
}
