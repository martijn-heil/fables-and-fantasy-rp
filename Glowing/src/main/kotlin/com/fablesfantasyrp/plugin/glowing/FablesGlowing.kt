package com.fablesfantasyrp.plugin.glowing

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin


val SYSPREFIX = "${DARK_RED}${BOLD}[${RED}${BOLD} GLOWING ${DARK_RED}${BOLD}]${GRAY}"


class FablesGlowing : JavaPlugin() {
	private lateinit var denizenGlowingManager: DenizenGlowingManager
	val glowingManager: GlowingManager
		get() = denizenGlowingManager

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		denizenGlowingManager = DenizenGlowingManager(this)
		denizenGlowingManager.start()
	}

	override fun onDisable() {
		denizenGlowingManager.stop()
	}

	companion object {
		lateinit var instance: FablesGlowing
	}
}
