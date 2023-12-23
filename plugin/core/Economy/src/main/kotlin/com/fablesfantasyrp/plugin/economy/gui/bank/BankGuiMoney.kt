package com.fablesfantasyrp.plugin.economy.gui.bank

import com.fablesfantasyrp.plugin.economy.CURRENCY_SYMBOL
import com.fablesfantasyrp.plugin.economy.SYSPREFIX
import com.fablesfantasyrp.plugin.economy.data.ProfileEconomyData
import com.fablesfantasyrp.plugin.economy.flaunch
import com.fablesfantasyrp.plugin.economy.formatMoney
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.itemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.meta
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.name
import com.github.shynixn.mccoroutine.bukkit.launch
import de.themoep.inventorygui.DynamicGuiElement
import de.themoep.inventorygui.InventoryGui
import de.themoep.inventorygui.StaticGuiElement
import kotlinx.coroutines.CompletableDeferred
import net.kyori.adventure.text.Component
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.min

class BankGuiMoney(plugin: JavaPlugin, private val data: ProfileEconomyData)
	: InventoryGui(plugin, "Bank Money", arrayOf("102345600"))
{
	init {
		this.addElements(
				StaticGuiElement('0', ItemStack(Material.BLACK_STAINED_GLASS_PANE), { true }, ""),
				StaticGuiElement('1', ItemStack(Material.RED_STAINED_GLASS_PANE), { true },
						"${ChatColor.RED}Back",
						"${ChatColor.RED}Go back"
				),
				StaticGuiElement('2', ItemStack(Material.RED_STAINED_GLASS_PANE), { c -> withdrawAll(c.whoClicked as Player); true },
						"${ChatColor.AQUA}Click to withdraw all money",
						"${ChatColor.GREEN}Withdraws all money"
				),
				StaticGuiElement('3', ItemStack(Material.RED_STAINED_GLASS_PANE), { c -> flaunch { withdraw(c.whoClicked as Player) }; true },
						"${ChatColor.AQUA}Click to withdraw money",
						"${ChatColor.GREEN}Enter the amount to withdraw"
				),
				DynamicGuiElement('4') { ->
					StaticGuiElement('4', ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE), { true },
							"${ChatColor.AQUA}Balance: ${formatMoney(data.bankMoney)}",
							"${ChatColor.GREEN}Your current amount of $CURRENCY_SYMBOL"
					)
				},
				StaticGuiElement('5', ItemStack(Material.GREEN_STAINED_GLASS_PANE), { c -> flaunch { deposit(c.whoClicked as Player) }; true },
						"${ChatColor.AQUA}Click to deposit money",
						"${ChatColor.GREEN}Enter the amount to deposit"
				),
				StaticGuiElement('6', ItemStack(Material.GREEN_STAINED_GLASS_PANE), { c -> depositAll(c.whoClicked as Player); true },
						"${ChatColor.AQUA}Click to deposit all money",
						"${ChatColor.GREEN}Deposits all money"
				))
	}

	fun withdrawAll(whoClicked: Player) {
		val amount = data.bankMoney
		data.money += amount
		data.bankMoney = 0
		whoClicked.sendMessage("$SYSPREFIX Withdrew ${CURRENCY_SYMBOL}$amount")
		draw()
	}

	suspend fun withdraw(whoClicked: Player) {
		val amount = min(data.bankMoney, promptAmount(whoClicked, "Withdraw"))
		check(amount <= data.bankMoney)
		data.bankMoney -= amount
		data.money += amount
		whoClicked.sendMessage("$SYSPREFIX Withdrew ${CURRENCY_SYMBOL}$amount")
		draw()
	}

	fun depositAll(whoClicked: Player) {
		val amount = data.money
		data.bankMoney += amount
		data.money = 0
		whoClicked.sendMessage("$SYSPREFIX Deposited ${CURRENCY_SYMBOL}$amount")
		draw()
	}

	suspend fun deposit(whoClicked: Player) {
		val amount = min(data.money, promptAmount(whoClicked, "Deposit"))
		check(amount <= data.money)
		whoClicked.sendMessage("$SYSPREFIX Deposited ${CURRENCY_SYMBOL}$amount")
		data.money -= amount
		data.bankMoney += amount
		draw()
	}

	private suspend fun promptAmount(who: Player, title: String): Int {
		val history = getHistory(who)
		this.close()
		val item = itemStack(Material.BLACK_STAINED_GLASS_PANE) { meta { name = Component.empty() } }
		val deferred = CompletableDeferred<UInt>()
		AnvilGUI.Builder()
				.plugin(plugin)
				.title(title)
				.itemLeft(item)
				.itemOutput(item)
				.onComplete { _, s ->
					val amount = s.toUIntOrNull() ?: return@onComplete AnvilGUI.Response.text("Invalid number '$s'")
					deferred.complete(amount)
					AnvilGUI.Response.close()
				}
				.onClose {
					deferred.cancel()
					show(who)
					history.forEach { addHistory(who, it) }
				}
				.open(who)
		return deferred.await().toInt()
	}
}
