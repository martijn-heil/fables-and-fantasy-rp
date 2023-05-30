package com.fablesfantasyrp.plugin.time

import org.bukkit.plugin.Plugin
import java.time.Instant

class GameClock(private val plugin: Plugin,
				private var startTime: Instant,
				private val speed: Int) : FablesInstantSource {
	private var isStopped = false
	private val server = plugin.server
	private var taskId: Int = 0
	var milliseconds = startTime.toEpochMilli()

	fun start() {
		taskId = server.scheduler.scheduleSyncRepeatingTask(plugin, {
			if (!isStopped) milliseconds += 50*speed
		}, 0, 1)
		check(taskId != -1)
	}

	fun stop() {
		isStopped = true
		server.scheduler.cancelTask(taskId)
	}

	override fun instant() = Instant.ofEpochMilli(milliseconds)
}
