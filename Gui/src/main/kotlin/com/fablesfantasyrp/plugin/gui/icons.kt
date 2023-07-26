package com.fablesfantasyrp.plugin.gui

import com.fablesfantasyrp.plugin.utils.customModel
import com.fablesfantasyrp.plugin.utils.itemStack
import com.fablesfantasyrp.plugin.utils.meta
import com.fablesfantasyrp.plugin.utils.name
import net.kyori.adventure.text.Component
import org.bukkit.Material

object Icon {
	val CHECKMARK = itemStack(Material.EMERALD) 		{ meta { customModel = 1; name = Component.empty() } }
	val X = itemStack(Material.REDSTONE) 				{ meta { customModel = 1; name = Component.empty() } }
	val TRASH_BIN = itemStack(Material.HOPPER_MINECART) { meta { customModel = 1; name = Component.empty() } }
}
