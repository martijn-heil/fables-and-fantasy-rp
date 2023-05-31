package com.fablesfantasyrp.plugin.time

import org.bukkit.World
import org.bukkit.plugin.Plugin
import kotlin.math.roundToLong

private const val SECONDS_IN_DAY = 86400
private const val TICKS_IN_DAY = 24000
private const val TICKS_IN_SECOND: Double = TICKS_IN_DAY.toDouble() / SECONDS_IN_DAY.toDouble()

class WorldTimeSynchronizer(private val plugin: Plugin,
							private val worlds: Collection<World>,
							private val clock: FablesInstantSource) {
	private val server = plugin.server
	private var taskId: Int = 0

	fun start() {
		taskId = server.scheduler.scheduleSyncRepeatingTask(plugin, {
			val epochSecond = clock.instant().epochSecond
			// Offset by -6000 because 0 ticks in Minecraft is actually 6 AM instead of midnight
			val time = (epochSecond.toDouble() * TICKS_IN_SECOND).roundToLong() - 6000
			worlds.forEach { it.time = time }
		}, 0, 1)
		check(taskId != -1)
	}

	fun stop() {
		server.scheduler.cancelTask(taskId)
	}
}
