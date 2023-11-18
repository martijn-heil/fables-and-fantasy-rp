package com.fablesfantasyrp.plugin.playeresp.interop.staffmode

import com.fablesfantasyrp.plugin.playeresp.PlayerEspManager
import com.fablesfantasyrp.plugin.playeresp.SYSPREFIX
import com.fablesfantasyrp.plugin.staffmode.event.PlayerSwitchStaffDutyModeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class StaffModeHook(private val plugin: Plugin, private val espManager: PlayerEspManager) {
	private val server = plugin.server

	fun start() {
		server.pluginManager.registerEvents(StaffModeHookListener(), plugin)
	}

	private inner class StaffModeHookListener : Listener {
		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onPlayerSwitchStaffDutyMode(e: PlayerSwitchStaffDutyModeEvent) {
			if (!e.goOnDuty && espManager.hasEsp(e.player)) {
				espManager.setEsp(e.player, false)
				e.player.sendMessage("$SYSPREFIX Your PlayerESP was turned off because you are going off duty.")
			}
		}
	}
}
