package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*

class PlayerInstanceOccupiedException : Exception()

class PlayerInstanceManager(private val server: Server) {
	private val currentInstances = HashMap<UUID, PlayerInstance>()
	private val currentInstancesTwo = HashMap<PlayerInstance, UUID>()

	fun getCurrentForPlayer(player: Player) = currentInstances[player.uniqueId]

	@Throws(PlayerInstanceOccupiedException::class)
	fun setCurrentForPlayer(player: Player, playerInstance: PlayerInstance) {
		if (this.getCurrentForPlayer(player) == playerInstance) return
		val currentHolder = currentInstancesTwo[playerInstance]
		if (currentHolder != null) throw PlayerInstanceOccupiedException()
		val event = PlayerSwitchPlayerInstanceEvent(player, getCurrentForPlayer(player), playerInstance)
		server.pluginManager.callEvent(event)
		currentInstances[player.uniqueId] = playerInstance
		currentInstancesTwo[playerInstance] = player.uniqueId
	}

	fun stopTracking(player: Player) {
		val result = currentInstances.remove(player.uniqueId)
		currentInstancesTwo.remove(result)
	}

	fun getCurrentForPlayerInstance(instance: PlayerInstance): Player? {
		return currentInstancesTwo[instance]?.let { server.getPlayer(it) }
	}
}
