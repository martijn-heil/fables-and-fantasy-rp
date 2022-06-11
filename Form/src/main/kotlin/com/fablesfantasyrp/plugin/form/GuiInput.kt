package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.gui.ResultProducingGui
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer


suspend fun<T> promptGui(p: FablesPlayer, gui: ResultProducingGui<T>): T {
	return gui.execute(p.player)
}
