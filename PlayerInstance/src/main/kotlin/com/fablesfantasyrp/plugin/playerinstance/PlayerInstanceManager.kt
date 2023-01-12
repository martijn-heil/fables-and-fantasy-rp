package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.event.PostPlayerSwitchPlayerInstanceEvent
import com.fablesfantasyrp.plugin.playerinstance.event.PrePlayerSwitchPlayerInstanceEvent
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
		val currentPlayerInstance = this.getCurrentForPlayer(player)
		if (currentPlayerInstance == playerInstance) return
		val currentHolder = currentInstancesTwo[playerInstance]
		if (currentHolder != null) throw PlayerInstanceOccupiedException()

		server.pluginManager.callEvent(PrePlayerSwitchPlayerInstanceEvent(player, getCurrentForPlayer(player), playerInstance))

		currentInstancesTwo.remove(currentPlayerInstance)
		currentInstances[player.uniqueId] = playerInstance
		currentInstancesTwo[playerInstance] = player.uniqueId

		server.pluginManager.callEvent(PostPlayerSwitchPlayerInstanceEvent(player, getCurrentForPlayer(player), playerInstance))
	}

	fun stopTracking(player: Player) {
		val result = currentInstances.remove(player.uniqueId)
		currentInstancesTwo.remove(result)
	}

	fun getCurrentForPlayerInstance(instance: PlayerInstance): Player? {
		return currentInstancesTwo[instance]?.let { server.getPlayer(it) }
	}
}
