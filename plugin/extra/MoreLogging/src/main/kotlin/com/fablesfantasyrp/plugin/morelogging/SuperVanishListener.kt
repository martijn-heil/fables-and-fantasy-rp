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
package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.ToggleableState
import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.logging.Level
import java.util.logging.Logger

class SuperVanishListener(private val logger: Logger, private val plugin: Plugin) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerVanishStateChange(e: PlayerVanishStateChangeEvent) {
		val player = plugin.server.getOfflinePlayer(e.uuid)
		val newState = ToggleableState.fromIsActiveBoolean(e.isVanishing)
		val oldState = !newState
		logPlayerStateChange(logger, Level.FINE, player, "VANISH", oldState.toString(), newState.toString())
	}
}
