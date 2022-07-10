package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.gui.ResultProducingGui
import org.bukkit.entity.Player


suspend fun<T> promptGui(p: Player, gui: ResultProducingGui<T>): T {
	return gui.execute(p)
}
