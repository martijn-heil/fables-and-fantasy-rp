package com.fablesfantasyrp.plugin.gui

import org.bukkit.entity.Player

interface ResultProducingGui<T> {
	suspend fun execute(who: Player): T
}
