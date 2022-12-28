package com.fablesfantasyrp.plugin.leadbreakingsound

import com.fablesfantasyrp.plugin.leadbreakingsound.listeners.LeadBreakingListener
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} LEAD BREAKING SOUND ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesLeadBreakingSound.instance

class FablesLeadBreakingSound : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		server.pluginManager.registerEvents(LeadBreakingListener(), this)
	}

	companion object {
		lateinit var instance: FablesLeadBreakingSound
			private set
	}
}
