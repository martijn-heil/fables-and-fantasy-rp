package com.fablesfantasyrp.plugin.magic.objects

import kotlinx.coroutines.coroutineScope
import org.bukkit.Location
import org.bukkit.entity.Player

interface MagicAnimation {
	suspend fun execute(player: Player, location: Location)
}
