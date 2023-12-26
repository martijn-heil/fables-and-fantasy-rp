package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.utils.every
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistAddedPlayerEvent
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistRemovedPlayerEvent
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import org.bukkit.craftbukkit.v1_20_R2.CraftServer
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class WhitelistMonitor(private val plugin: JavaPlugin) {
	private val server = plugin.server
	private var isStopped = false
	private lateinit var lastWhitelist: Set<UUID>

	fun start() {
		plugin.launch {
			lastWhitelist = getWhitelist().await()

			every(plugin, 50.milliseconds) {
				if (isStopped) { this.cancel(); return@every }
				val whitelist = getWhitelist().await()

				// These .minus() operations take about 0.02 ms or less for each to run on our production server hardware
				// And considering that they're executed off the main thread, we really shouldn't worry about it.
				val removedPlayers 	= async { lastWhitelist.minus(whitelist) }
				val addedPlayers 	= async { whitelist.minus(lastWhitelist) }

				removedPlayers.await().forEach {
					server.pluginManager.callEvent(WhitelistRemovedPlayerEvent(server.getOfflinePlayer(it)))
				}

				addedPlayers.await().forEach {
					server.pluginManager.callEvent(WhitelistAddedPlayerEvent(server.getOfflinePlayer(it)))
				}

				lastWhitelist = whitelist
			}
		}
	}

	fun stop() {
		isStopped = true
	}

	private fun getWhitelist(): Deferred<Set<UUID>> {
		val whitelist = (server as CraftServer).handle.whiteList.entries
		return plugin.scope.async { whitelist.asSequence().mapNotNull { it.user?.id }.toHashSet() }
	}
}
