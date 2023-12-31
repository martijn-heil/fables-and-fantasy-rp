package com.fablesfantasyrp.plugin.shops.gui

import com.fablesfantasyrp.plugin.economy.CURRENCY_NAME
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.gui.Icon
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.shops.SYSPREFIX
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.flaunch
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.*
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.ocpsoft.prettytime.PrettyTime
import java.time.Instant

class ShopCustomerGui(plugin: JavaPlugin,
					  private val shop: Shop,
					  private val profileManager: ProfileManager,
					  private val profileEconomyRepository: ProfileEconomyRepository)
	: InventoryGui(plugin, "", arrayOf(
	"    s    ",
	"    x  i ",
	"    b    ")) {
	init {
		flaunch { title = getShopTitle(shop) }

		if (shop.sellPrice > 0) {
			this.addElement(DynamicGuiElement('s') { _ ->
				StaticGuiElement('s', Icon.UP, { click ->
					(click.whoClicked as? Player)?.let { sell(it) }
					true
				},  "${ChatColor.GOLD}Sell ${getItemName()} for ${shop.sellPrice} $CURRENCY_NAME")
			})
		}

		if (shop.buyPrice > 0) {
			this.addElement(DynamicGuiElement('b') { _ ->
				StaticGuiElement('b', Icon.DOWN, { click ->
					(click.whoClicked as? Player)?.let { buy(it) }
					true
				}, "${ChatColor.GOLD}Buy ${getItemName()} for ${shop.buyPrice} $CURRENCY_NAME")
			})
		}

		this.addElement(StaticGuiElement('x', shop.item.asOne()))

		this.addElement(DynamicGuiElement('i') { _ ->
			StaticGuiElement('i', Icon.INFO,
				"${ChatColor.GOLD}Additional information\n" +
					"\n" +
					"${ChatColor.DARK_PURPLE}${ChatColor.ITALIC}Items in stock\n" +
					"${ChatColor.GRAY}${shop.stock}\n" +
					"\n" +
					"${ChatColor.DARK_PURPLE}${ChatColor.ITALIC}Last active\n" +
					"${ChatColor.GRAY}${PrettyTime().format(shop.lastActive)}")
		})
	}

	private fun sell(player: Player) {
		val profile = profileManager.getCurrentForPlayer(player) ?: run {
			player.sendError("Are you are currently not on a profile")
			return
		}
		val profileEconomy = profileEconomyRepository.forProfile(profile)

		val inventory = player.inventory
		val available = inventory.countSimilar(shop.item)
		if (available < shop.amount) {
			player.sendError("You do not have ${getItemName()}")
			return
		}

		inventory.withdrawSimilar(shop.item, shop.amount)
		shop.stock += shop.amount
		profileEconomy.money += shop.sellPrice
		player.sendMessage("$SYSPREFIX You sold ${getItemName()} for ${shop.sellPrice} $CURRENCY_NAME")
		shop.lastActive = Instant.now()
		draw()
	}

	private fun buy(player: Player) {
		val profile = profileManager.getCurrentForPlayer(player) ?: run {
			player.sendError("You are currently not on a profile")
			return
		}

		if (shop.stock < shop.amount) {
			player.sendError("The shop is out of stock!")
			return
		}

		val profileEconomy = profileEconomyRepository.forProfile(profile)
		if (profileEconomy.money < shop.buyPrice) {
			player.sendError("You do not have enough funds to buy ${getItemName()}")
			return
		}

		profileEconomy.money -= shop.buyPrice
		shop.stock -= shop.amount
		val remainder = player.inventory.deposit(shop.item.asQuantity(shop.amount))
		remainder?.splitStacks()?.forEach { player.location.world.dropItem(player.location, it) }
		player.sendMessage("$SYSPREFIX You bought ${getItemName()} for ${shop.buyPrice} $CURRENCY_NAME")
		shop.lastActive = Instant.now()
		draw()
	}

	private fun getItemName() = shop.item.formatNameWithAmount(shop.amount)
}
