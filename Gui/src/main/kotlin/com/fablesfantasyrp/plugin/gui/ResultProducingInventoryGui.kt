package com.fablesfantasyrp.plugin.gui

import de.themoep.inventorygui.InventoryGui
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class ResultProducingInventoryGui<T>(plugin: JavaPlugin, title: String, rows: Array<String>)
	: InventoryGui(plugin, title, rows), ResultProducingGui<T> {
	protected val result = CompletableDeferred<T>()

	init {
		this.setCloseAction {
			if (!result.isCompleted) {
				result.cancel(CancellationException("Cancelled by player"))
			}
			true
		}
	}

	suspend fun execute(who: HumanEntity): T {
		this.show(who)
		val result = result.await()
		this.close(false)
		return result
	}

	override suspend fun execute(who: Player): T = this.execute(who as HumanEntity)
}
