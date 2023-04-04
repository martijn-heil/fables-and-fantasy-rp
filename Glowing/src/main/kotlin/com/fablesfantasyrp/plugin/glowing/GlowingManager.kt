package com.fablesfantasyrp.plugin.glowing

import org.bukkit.ChatColor
import org.bukkit.entity.Player

interface GlowingManager {
	fun isGlowingFor(glowing: Player, viewing: Player): Boolean
	fun setIsGlowingFor(glowing: Player, viewing: Player, value: Boolean)
	fun setGlowColor(glowing: Player, value: ChatColor?)
	fun getGlowColor(glowing: Player): ChatColor?
	val defaultGlowColor: ChatColor
}
