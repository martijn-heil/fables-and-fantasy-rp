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
package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.entity.Horse
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.spigotmc.event.entity.EntityMountEvent

class HorseJumpStrength(private val plugin: Plugin) : Mechanic {
	val MAX_JUMP_STRENGTH = 0.6

	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(HorseJumpStrengthListener(), plugin)
	}

	inner class HorseJumpStrengthListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerMountHorse(e: EntityMountEvent) {
			val horse = e.mount as? Horse ?: return
			if (horse.jumpStrength > MAX_JUMP_STRENGTH) {
				horse.jumpStrength = MAX_JUMP_STRENGTH
			}
		}
	}
}
