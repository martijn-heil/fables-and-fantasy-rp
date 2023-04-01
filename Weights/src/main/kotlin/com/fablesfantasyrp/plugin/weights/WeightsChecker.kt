package com.fablesfantasyrp.plugin.weights

import org.bukkit.plugin.Plugin

class WeightsChecker(private val plugin: Plugin, private val config: WeightsConfig) {
	private val server = plugin.server
	private var taskId: Int = -1

	fun start() {
		taskId = server.scheduler.scheduleSyncRepeatingTask(plugin, {
			for (player in server.onlinePlayers) {
				val items = player.inventory.contents.filterNotNull()
				val weight = calculateWeight(items, config)
				applyWeight(player, weight, config.cap)
			}
		}, 0, 1)
		check(taskId != -1)
	}

	fun stop() {
		server.scheduler.cancelTask(taskId)
	}
}
