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
package com.fablesfantasyrp.plugin.shops.gui

import com.fablesfantasyrp.plugin.economy.CURRENCY_NAME
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.gui.Icon
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.shops.SYSPREFIX
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.validation.CommandValidationException
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.ocpsoft.prettytime.PrettyTime

class ShopCustomerGui(plugin: JavaPlugin,
					  title: String,
					  private val shop: Shop,
					  private val profileManager: ProfileManager,
					  private val profileEconomyRepository: ProfileEconomyRepository)
	: InventoryGui(plugin, title, arrayOf(
	"    s    ",
	"    x  i ",
	"    b    ")) {
	init {
		if (shop.customersCanSell) {
			this.addElement(DynamicGuiElement('s') { _ ->
				StaticGuiElement('s', Icon.UP, { click ->
					playClickSound()
					(click.whoClicked as? Player)?.let {
						sell(it)
						draw()
					}
					true
				},  "${ChatColor.GOLD}Sell ${shop.itemName} for ${shop.sellPrice} $CURRENCY_NAME")
			})
		}

		if (shop.customersCanBuy) {
			this.addElement(DynamicGuiElement('b') { _ ->
				StaticGuiElement('b', Icon.DOWN, { click ->
					playClickSound()
					(click.whoClicked as? Player)?.let {
						buy(it)
						draw()
					}
					true
				}, "${ChatColor.GOLD}Buy ${shop.itemName} for ${shop.buyPrice} $CURRENCY_NAME")
			})
		}

		this.addElement(StaticGuiElement('x', shop.item.asOne()))

		this.addElement(DynamicGuiElement('i') { _ ->
			StaticGuiElement('i', Icon.INFO,
				"${ChatColor.GOLD}Additional information\n" +
					"\n" +
					"${ChatColor.DARK_PURPLE}${ChatColor.ITALIC}Items in stock\n" +
					"${ChatColor.GRAY}${if (shop.isPublic) "\u221E" else shop.stock}\n" +
					"\n" +
					"${ChatColor.DARK_PURPLE}${ChatColor.ITALIC}Last active\n" +
					"${ChatColor.GRAY}${PrettyTime().format(shop.lastActive)}")
		})
	}

	private fun sell(customerPlayer: Player) {
		val customerProfile = profileManager.getCurrentForPlayer(customerPlayer) ?: run {
			customerPlayer.sendError("Are you are currently not on a profile")
			return
		}
		val customerEconomy = profileEconomyRepository.forProfile(customerProfile)
		val ownerEconomy = shop.owner?.let { profileEconomyRepository.forProfile(it) }

		try {
			shop.sell(customerPlayer, customerEconomy, ownerEconomy)
		} catch (ex: CommandValidationException) {
			customerPlayer.sendError(ex.component)
			return
		}
		customerPlayer.sendMessage("$SYSPREFIX You sold ${shop.itemName} for ${shop.sellPrice} $CURRENCY_NAME")
	}

	private fun buy(customerPlayer: Player) {
		val customerProfile = profileManager.getCurrentForPlayer(customerPlayer) ?: run {
			customerPlayer.sendError("Are you are currently not on a profile")
			return
		}
		val customerEconomy = profileEconomyRepository.forProfile(customerProfile)
		val ownerEconomy = shop.owner?.let { profileEconomyRepository.forProfile(it) }

		try {
			shop.buy(customerPlayer, customerEconomy, ownerEconomy)
		} catch (ex: CommandValidationException) {
			customerPlayer.sendError(ex.component)
			return
		}

		customerPlayer.sendMessage("$SYSPREFIX You bought ${shop.itemName} for ${shop.buyPrice} $CURRENCY_NAME")
	}
}
