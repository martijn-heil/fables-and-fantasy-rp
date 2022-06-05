package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.utils.every
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistAddedPlayerEvent
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistRemovedPlayerEvent
import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlin.time.Duration.Companion.milliseconds

class WhitelistMonitor(private val plugin: SuspendingJavaPlugin) {
	private val server = plugin.server
	private var isStopped = false
	private var lastWhitelist = server.whitelistedPlayers.toSet()

	fun start() {
		every(plugin, 50.milliseconds) {
			if (isStopped) { this.cancel(); return@every }
			val whitelist = server.whitelistedPlayers.toSet()

			val removedPlayers 	= async { lastWhitelist.minus(whitelist) }
			val addedPlayers 	= async { whitelist.minus(lastWhitelist) }

			removedPlayers.await().forEach { server.pluginManager.callEvent(WhitelistRemovedPlayerEvent(it)) }
			addedPlayers.await().forEach { server.pluginManager.callEvent(WhitelistAddedPlayerEvent(it)) }
			lastWhitelist = whitelist
		}
	}

	fun stop() {
		isStopped = true
	}
}
