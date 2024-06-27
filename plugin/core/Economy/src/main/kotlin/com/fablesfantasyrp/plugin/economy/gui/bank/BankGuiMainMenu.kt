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
package com.fablesfantasyrp.plugin.economy.gui.bank

import com.fablesfantasyrp.plugin.economy.CURRENCY_SYMBOL
import com.fablesfantasyrp.plugin.economy.data.ProfileEconomyData
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class BankGuiMainMenu(plugin: JavaPlugin, data: ProfileEconomyData) : InventoryGui(plugin, "Bank Main Menu",
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
