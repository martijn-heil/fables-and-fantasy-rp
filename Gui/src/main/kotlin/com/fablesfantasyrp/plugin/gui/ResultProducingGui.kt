package com.fablesfantasyrp.plugin.gui

import de.themoep.inventorygui.InventoryGui
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import org.bukkit.entity.HumanEntity
import org.bukkit.plugin.java.JavaPlugin

abstract class ResultProducingGui<T>(plugin: JavaPlugin, title: String, rows: Array<String>) : InventoryGui(plugin, title, rows) {
	protected val result = CompletableDeferred<T>()

	init {
		this.setCloseAction {
			if (!result.isCompleted) {
				result.cancel(CancellationException("Cancelled by player"))
			}
			false
		}
	}

	suspend fun execute(who: HumanEntity): T {
		this.show(who)
		val result = result.await()
		this.close(true)
		return result
	}
}
