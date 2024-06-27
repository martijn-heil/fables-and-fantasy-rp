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
package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.distanceSafe
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isVanished
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.Plugin
import kotlin.math.floor
import kotlin.random.Random

class TeleportParticlesListener(private val plugin: Plugin) : Listener {
	private val teleportCauses = hashSetOf(
		PlayerTeleportEvent.TeleportCause.PLUGIN,
		PlayerTeleportEvent.TeleportCause.COMMAND,
		PlayerTeleportEvent.TeleportCause.UNKNOWN
	)

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPlayerTeleport(e: PlayerTeleportEvent) {
		if (e.player.gameMode == GameMode.SPECTATOR) return
		if (e.player.isVanished) return
		if (!teleportCauses.contains(e.cause)) return

		playSound(e.from, e.to)

		if (e.from.world != e.to.world || e.from.distanceSquared(e.to) >= 25) {
			showParticles(e.from, e.to)
		}
	}

	private fun playSound(from: Location, to: Location) {
		flaunch {
			playSound(from)

			plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
				playSound(to)
			}, if (from.distanceSafe(to) >= 5) 1L else 0L)
		}
	}

	private fun showParticles(from: Location, to: Location) {
		val mutableTo = to.clone()

		if (from.world != to.world || from.distanceSquared(to) >= 25) {
			spawnSmoke(from, 5f)
			spawnSmoke(from.add(0.00, 1.00, 0.00), 5f)
			from.world.playEffect(from, Effect.MOBSPAWNER_FLAMES, null)
			plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
				for (i in 0..2) {
					mutableTo.world.playEffect(to, Effect.ENDER_SIGNAL, null)
					mutableTo.world.playEffect(to, Effect.ENDER_SIGNAL, null)
					mutableTo.add(0.00, 1.00, 0.00)
				}
			}, 1L)
		}
	}

	private fun playSound(location: Location) {
		location.world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.3f, 1f)
	}

	private fun spawnSmoke(location: Location, thickness: Float) {
		val singles = floor((thickness * 9).toDouble()).toInt()
		for (i in 0 until singles) {
			location.getWorld().playEffect(location, Effect.SMOKE, Random.nextInt(9))
		}
	}
}
