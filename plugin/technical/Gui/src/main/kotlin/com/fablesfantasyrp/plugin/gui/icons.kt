package com.fablesfantasyrp.plugin.gui

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.customModel
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.itemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.meta
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.name
import net.kyori.adventure.text.Component
import org.bukkit.Material

object Icon {
	val CHECKMARK = itemStack(Material.EMERALD) 		{ meta { customModel = 1; name = Component.empty() } }
	val X = itemStack(Material.REDSTONE) 				{ meta { customModel = 1; name = Component.empty() } }
	val TRASH_BIN = itemStack(Material.HOPPER_MINECART) { meta { customModel = 1; name = Component.empty() } }
	val UP = itemStack(Material.MAP) 					{ meta { customModel = 2; name = Component.empty() } }
	val DOWN = itemStack(Material.FILLED_MAP) 			{ meta { customModel = 2; name = Component.empty() } }
	val INFO = itemStack(Material.SUNFLOWER)			{ meta { customModel = 1; name = Component.empty() }}

	val ANDROS = itemStack(Material.PAPER) 				{}

	fun digit(digit: Int) = itemStack(Material.PLAYER_HEAD) { meta { customModel = digit } }
}
