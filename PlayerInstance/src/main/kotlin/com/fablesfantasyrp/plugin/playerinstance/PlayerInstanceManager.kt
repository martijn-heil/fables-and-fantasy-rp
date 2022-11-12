package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.entity.Player

class PlayerInstanceManager {
	private val currentInstances = HashMap<Player, PlayerInstance>()

	fun getCurrentForPlayer(player: Player) = currentInstances[player]
	fun setCurrentForPlayer(player: Player, playerInstance: PlayerInstance) { currentInstances[player] = playerInstance }
	fun stopTracking(player: Player) { currentInstances.remove(player) }
}
