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
import com.fablesfantasyrp.plugin.utils.RateLimiter
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isVanished
import org.bukkit.GameMode
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.util.*
import kotlin.random.Random

class HorseTrampling(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server
	private val rateLimiter = RateLimiter<UUID>(Duration.ofSeconds(2))

	override fun init() {
		server.pluginManager.registerEvents(HorseTramplingListener(), plugin)
		server.scheduler.scheduleSyncRepeatingTask(plugin, { rateLimiter.tick() }, 0, 1)
	}

	inner class HorseTramplingListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerRidesHorse(e: PlayerMoveEvent) {
			val player = e.player
			val horse = player.vehicle as? Horse ?: return

			val targets = horse.getNearbyEntities(0.5, 1.0, 0.5)
				.asSequence()
				.mapNotNull { it as? Player }
				.filter { !it.isInsideVehicle }
				.filter { !it.isInvulnerable }
				.filter { it.gameMode != GameMode.CREATIVE && it.gameMode != GameMode.SPECTATOR }
				.filter { !it.isVanished }
				.filter { !rateLimiter.rateLimit(it.uniqueId) }
				.toList()
			if (targets.isEmpty()) return

			val damage = Random.nextInt(5, 11).toDouble()

			val vector = e.to.toVector().subtract(e.from.toVector())
			val velocity = vector.multiply(1.5)

			targets.forEach {
				it.damage(damage)
				it.velocity = it.velocity.add(velocity)
			}
		}
	}
}
