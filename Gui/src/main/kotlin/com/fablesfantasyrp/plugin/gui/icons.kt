package com.fablesfantasyrp.plugin.gui

import com.fablesfantasyrp.plugin.utils.customModel
import com.fablesfantasyrp.plugin.utils.itemStack
import com.fablesfantasyrp.plugin.utils.meta
import org.bukkit.Material

object Icon {
	val CHECKMARK = itemStack(Material.EMERALD) { meta { customModel = 1 } }
	val X = itemStack(Material.REDSTONE) { meta { customModel = 1 } }
}
