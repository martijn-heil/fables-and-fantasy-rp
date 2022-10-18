package com.fablesfantasyrp.plugin.targeting

import org.bukkit.entity.Player

interface GlowingManager {
	fun glowFor(glowing: Player, viewing: Player)
	fun unglowFor(glowing: Player, viewing: Player)
}
