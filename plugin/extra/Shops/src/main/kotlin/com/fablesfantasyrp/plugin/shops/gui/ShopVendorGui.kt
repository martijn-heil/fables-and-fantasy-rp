package com.fablesfantasyrp.plugin.shops.gui

import com.fablesfantasyrp.plugin.gui.Icon
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.deposit
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.withdrawSimilar
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class ShopVendorGui(plugin: JavaPlugin, private val shop: Shop) : InventoryGui(plugin, "The Shop", arrayOf(
	"d 1      ",
	"s r i ovc",
	"w 2      ")) {

	init {
		this.addElement(StaticGuiElement('d', Icon.UP, { click ->
			val whoClicked = click.whoClicked
			val toDeposit = whoClicked.inventory.withdrawSimilar(shop.item).amount
			shop.stock += toDeposit
			true
		}, "${ChatColor.GOLD}Deposit all items"))

		this.addElement(StaticGuiElement('w', Icon.DOWN, { click ->
			val whoClicked = click.whoClicked
			val item = shop.item.clone().asQuantity(shop.stock)
			val remainder = whoClicked.inventory.deposit(item)
			shop.stock = remainder?.amount ?: 0
			true
		}, "${ChatColor.GOLD}Withdraw all items"))

		this.addElement(DynamicGuiElement('s') { _ ->
			StaticGuiElement('s', shop.item.asQuantity(shop.stock),
				"${ChatColor.GOLD}In stock: ${shop.stock}")
		})

		this.addElement(StaticGuiElement('1', Icon.UP, { shop.amount++; true }))
		this.addElement(StaticGuiElement('2', Icon.DOWN, { shop.amount--; true }))

		this.addElement(DynamicGuiElement('r') { _ ->
			StaticGuiElement('r', ItemStack(Material.MINECART),
				"${ChatColor.GOLD}Rate\n" +
					"${ChatColor.GRAY}Amount per click: ${shop.amount}\n")
		})

		this.addElement(DynamicGuiElement('i') { _ ->
			StaticGuiElement('i', shop.item.asQuantity(1))
		})
	}
}
