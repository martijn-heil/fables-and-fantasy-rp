package com.fablesfantasyrp.plugin.economy.gui.bank

import com.fablesfantasyrp.plugin.economy.CURRENCY_SYMBOL
import com.fablesfantasyrp.plugin.economy.data.PlayerInstanceEconomyData
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class BankGuiMainMenu(plugin: JavaPlugin, data: PlayerInstanceEconomyData) : InventoryGui(plugin, "Bank Main Menu",
		arrayOf("000100000"),
		StaticGuiElement('0', ItemStack(Material.BLACK_STAINED_GLASS_PANE), { c ->
			BankGuiMoney(plugin, data).show(c.whoClicked)
			true
		}, ""),
		DynamicGuiElement('1') { ->
			StaticGuiElement('1', ItemStack(Material.GREEN_STAINED_GLASS_PANE), { p -> BankGuiMoney(plugin, data).show(p.whoClicked); true },
					"${ChatColor.AQUA}Balance: $CURRENCY_SYMBOL${"%,d".format(data.bankMoney)}",
					"${ChatColor.GREEN}Your current amount of $CURRENCY_SYMBOL"
			)
		})
