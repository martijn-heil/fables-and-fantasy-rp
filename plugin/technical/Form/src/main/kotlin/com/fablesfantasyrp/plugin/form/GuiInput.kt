package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.gui.ResultProducingInventoryGui
import org.bukkit.entity.Player


suspend fun<T> promptGui(p: Player, gui: ResultProducingInventoryGui<T>): T {
	return gui.execute(p)
}
