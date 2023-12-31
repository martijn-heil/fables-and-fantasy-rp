package com.fablesfantasyrp.plugin.shops.gui

import com.fablesfantasyrp.plugin.economy.CURRENCY_NAME
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.gui.Icon
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.shops.SYSPREFIX
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import com.fablesfantasyrp.plugin.shops.flaunch
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.deposit
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.withdrawSimilar
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.max
import kotlin.math.min

class ShopVendorGui(plugin: JavaPlugin,
					private val shop: Shop,
					private val shops: ShopRepository,
					private val profileManager: ProfileManager,
					private val profileEconomyRepository: ProfileEconomyRepository) : InventoryGui(plugin, "", arrayOf(
	"d135    t",
	"arbs i   ",
	"w246    c")) {

	init {
		flaunch { title = getShopTitle(shop) }

		initStock()
		initRate()
		initBuying()
		initSelling()

		this.addElement(DynamicGuiElement('i') { _ ->
			StaticGuiElement('i', shop.item.asQuantity(1))
		})

		this.addElement(StaticGuiElement('c', ItemStack(Material.OAK_SIGN), { click ->
			ShopCustomerGui(plugin, shop, profileManager, profileEconomyRepository).show(click.whoClicked)
			true
		}, "${ChatColor.GOLD}View as customer"))

		this.addElement(StaticGuiElement('t', Icon.TRASH_BIN, { click ->
			val whoClicked = click.whoClicked
			flaunch {
				if (shop.stock != 0) {
					whoClicked.sendError("This shop is not empty. Take out all of the stock before destroying it.")
					return@flaunch
				}

				close(true)

				try {
					shops.destroy(shop)
				} catch (ex: Exception) {
					whoClicked.sendError("An internal server error occurred while trying to destroy shop #${shop.id}")
					throw ex
				}
				whoClicked.sendMessage("$SYSPREFIX Destroyed shop #${shop.id}")
			}
			true
		}, "${ChatColor.RED}Destroy shop"))
	}

	private fun initStock() {
		this.addElement(StaticGuiElement('d', Icon.UP, { click ->
			val whoClicked = click.whoClicked
			val toDeposit = whoClicked.inventory.withdrawSimilar(shop.item).amount
			shop.stock += toDeposit
			playClickSound()
			draw()
			true
		}, "${ChatColor.GOLD}Deposit all items"))

		this.addElement(StaticGuiElement('w', Icon.DOWN, { click ->
			val whoClicked = click.whoClicked
			val item = shop.item.clone().asQuantity(shop.stock)
			val remainder = whoClicked.inventory.deposit(item)
			shop.stock = remainder?.amount ?: 0
			playClickSound()
			draw()
			true
		}, "${ChatColor.GOLD}Withdraw all items"))

		this.addElement(DynamicGuiElement('a') { _ ->
			StaticGuiElement('s', ItemStack(Material.CHEST_MINECART),
				"${ChatColor.GOLD}In stock: ${shop.stock}")
		})
	}

	private fun initRate() {
		this.addElement(StaticGuiElement('1', Icon.UP, {
			if (shop.amount > 0) shop.amount = min(64, shop.amount * 2) else shop.amount = 1
			playClickSound()
			draw()
			true
		}, "${ChatColor.GREEN}+"))

		this.addElement(StaticGuiElement('2', Icon.DOWN, {
			shop.amount = max(1, shop.amount / 2)
			playClickSound()
			draw()
			true
		}, "${ChatColor.RED}-"))

		this.addElement(DynamicGuiElement('r') { _ ->
			StaticGuiElement('r', ItemStack(Material.MINECART),
				"${ChatColor.GOLD}Rate\n" +
					"${ChatColor.GRAY}Amount per click: ${shop.amount}\n")
		})
	}

	private fun initBuying() {
		this.addElement(DynamicGuiElement('b') { _ ->
			StaticGuiElement('b', Icon.ANDROS, {
				true
			}, "${ChatColor.GOLD}Customers can buy for: ${shop.buyPrice} ${CURRENCY_NAME}\n" +
				"${ChatColor.GRAY}(zero means buying is disabled)")
		})

		this.addElement(StaticGuiElement('3', Icon.UP, {
			shop.buyPrice = max(shop.buyPrice, shop.buyPrice + 1)
			draw()
			true
		}, "${ChatColor.GREEN}+"))

		this.addElement(StaticGuiElement('4', Icon.DOWN, {
			shop.buyPrice = max(0, shop.buyPrice - 1)
			draw()
			true
		}, "${ChatColor.RED}-"))
	}

	private fun initSelling() {
		this.addElement(DynamicGuiElement('s') { _ ->
			StaticGuiElement('s', Icon.ANDROS, {
				true
			}, "${ChatColor.GOLD}Customers can sell for: ${shop.sellPrice} ${CURRENCY_NAME}\n" +
				"${ChatColor.GRAY}(zero means selling is disabled)")
		})

		this.addElement(StaticGuiElement('5', Icon.UP, {
			shop.sellPrice = max(shop.sellPrice, shop.sellPrice + 1)
			draw()
			true
		}, "${ChatColor.GREEN}+"))

		this.addElement(StaticGuiElement('6', Icon.DOWN, {
			shop.sellPrice = max(0, shop.sellPrice - 1)
			draw()
			true
		}, "${ChatColor.RED}-"))
	}

}
