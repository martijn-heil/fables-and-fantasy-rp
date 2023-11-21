package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.distanceSafe
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isVanished
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.Effect
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
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPlayerTeleport(e: PlayerTeleportEvent) {
		if (e.player.isVanished) return

		playSound(e.from, e.to)

		if (e.from.world != e.to.world || e.from.distanceSquared(e.to) >= 25) {
			showParticles(e.from, e.to)
		}
	}

	private fun playSound(from: Location, to: Location) {
		plugin.launch {
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
