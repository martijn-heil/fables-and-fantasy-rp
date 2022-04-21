package com.fablesfantasyrp.plugin.whitelist

import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin

internal val SYSPREFIX = "${DARK_RED}${BOLD}[${RED}${BOLD}WHITELIST${DARK_RED}${BOLD}]${GRAY}"

class FablesWhitelist : JavaPlugin() {

	override fun onEnable() {
		instance = this
		server.pluginManager.registerEvents(WhitelistListener(server), this)
	}

	companion object {
		lateinit var instance: FablesWhitelist
	}
}
