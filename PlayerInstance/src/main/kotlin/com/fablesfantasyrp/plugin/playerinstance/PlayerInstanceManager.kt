package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.Server
import org.bukkit.entity.Player

class PlayerInstanceManager(private val server: Server) {
	private val currentInstances = HashMap<Player, PlayerInstance>()

	fun getCurrentForPlayer(player: Player) = currentInstances[player]
	fun setCurrentForPlayer(player: Player, playerInstance: PlayerInstance) {
		if (this.getCurrentForPlayer(player) == playerInstance) return
		val event = PlayerSwitchPlayerInstanceEvent(player, getCurrentForPlayer(player), playerInstance)
		server.pluginManager.callEvent(event)
		currentInstances[player] = playerInstance
	}
	fun stopTracking(player: Player) { currentInstances.remove(player) }
}
