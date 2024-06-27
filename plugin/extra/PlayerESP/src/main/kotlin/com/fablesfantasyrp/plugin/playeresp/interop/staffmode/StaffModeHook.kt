/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
