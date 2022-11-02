package com.fablesfantasyrp.plugin.glowing

import org.bukkit.entity.Player

interface GlowingManager {
	fun isGlowingFor(glowing: Player, viewing: Player): Boolean
	fun setIsGlowingFor(glowing: Player, viewing: Player, value: Boolean)
}
