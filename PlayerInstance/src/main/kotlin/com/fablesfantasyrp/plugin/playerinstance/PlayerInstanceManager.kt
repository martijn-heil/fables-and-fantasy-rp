package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*

class PlayerInstanceManager(private val server: Server) {
	private val currentInstances = HashMap<UUID, PlayerInstance>()

	fun getCurrentForPlayer(player: Player) = currentInstances[player.uniqueId]
	fun setCurrentForPlayer(player: Player, playerInstance: PlayerInstance) {
		if (this.getCurrentForPlayer(player) == playerInstance) return
		val event = PlayerSwitchPlayerInstanceEvent(player, getCurrentForPlayer(player), playerInstance)
		server.pluginManager.callEvent(event)
		currentInstances[player.uniqueId] = playerInstance
	}
	fun stopTracking(player: Player) { currentInstances.remove(player.uniqueId) }

	fun getCurrentForPlayerInstance(instance: PlayerInstance): Player? {
		return currentInstances.entries.find { it.value == instance }?.key?.let { server.getPlayer(it) }
	}
}
