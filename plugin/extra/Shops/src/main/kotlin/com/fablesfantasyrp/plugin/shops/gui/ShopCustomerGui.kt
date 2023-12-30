package com.fablesfantasyrp.plugin.shops.gui

import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.economy.formatMoney
import com.fablesfantasyrp.plugin.gui.Icon
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.shops.SYSPREFIX
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.countSimilar
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.fancyName
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.withdrawSimilar
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ShopCustomerGui(plugin: JavaPlugin,
					  private val shop: Shop,
					  private val profileEconomyRepository: ProfileEconomyRepository,
					  private val profileManager: ProfileManager)
	: InventoryGui(plugin, "Shop #${shop.id}", arrayOf(
	"    s    ",
	"    x    ",
	"    b    ")) {
	init {
		if (shop.sellPrice > 0) {
			this.addElement(DynamicGuiElement('s') { _ ->
				StaticGuiElement('s', Icon.UP, { click ->
					(click.whoClicked as? Player)?.let { sell(it) }
					true
				},  "${ChatColor.GOLD}Sell ${getDescription()} for ${shop.sellPrice}")
			})
		}

		if (shop.buyPrice > 0) {
			this.addElement(DynamicGuiElement('b') { _ ->
				StaticGuiElement('b', Icon.UP, { click ->
					(click.whoClicked as? Player)?.let { buy(it) }
					true
				}, "${ChatColor.GOLD}Buy ${getDescription()} for ${shop.sellPrice}")
			})
		}

		this.addElement(StaticGuiElement('x', shop.item.asOne()))
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
			player.sendError("You do not have ${getDescription()}")
			return
		}

		inventory.withdrawSimilar(shop.item)
		profileEconomy.money += shop.sellPrice
		player.sendMessage("$SYSPREFIX You sold ${getDescription()} for ${formatMoney(shop.buyPrice)}")
	}

	private fun buy(player: Player) {
		val profile = profileManager.getCurrentForPlayer(player) ?: run {
			player.sendError("Are you are currently not on a profile")
			return
		}
		val profileEconomy = profileEconomyRepository.forProfile(profile)

		if (profileEconomy.money < shop.buyPrice) {
			player.sendError("You do not have enough funds to buy ${getDescription()}")
			return
		}

		profileEconomy.money -= shop.buyPrice
		player.sendMessage("$SYSPREFIX You bought ${getDescription()} for ${formatMoney(shop.buyPrice)}")
	}

	private fun getItemName() = shop.item.asQuantity(shop.amount).fancyName
	private fun getDescription() = "${shop.amount} ${getItemName()}"
}
