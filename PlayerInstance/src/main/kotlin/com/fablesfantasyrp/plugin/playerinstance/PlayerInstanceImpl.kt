package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.event.PostPlayerSwitchPlayerInstanceEvent
import com.fablesfantasyrp.plugin.playerinstance.event.PrePlayerSwitchPlayerInstanceEvent
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*

internal class PlayerInstanceManagerImpl(private val server: Server) : PlayerInstanceManager {
	private val currentInstances = HashMap<UUID, PlayerInstance>()
	private val currentInstancesTwo = HashMap<PlayerInstance, UUID>()

	override fun getCurrentForPlayer(player: Player) = currentInstances[player.uniqueId]

	@Throws(PlayerInstanceOccupiedException::class)
	override fun setCurrentForPlayer(player: Player, playerInstance: PlayerInstance, force: Boolean) {
		val currentPlayerInstance = this.getCurrentForPlayer(player)
		if (currentPlayerInstance == playerInstance) return

		val currentHolder = currentInstancesTwo[playerInstance]
		if (currentHolder != null) {
			if (force) {
				this.stopTracking(server.getPlayer(currentHolder)!!)
			} else {
				throw PlayerInstanceOccupiedException(server.getPlayer(currentHolder)!!)
			}
		}

		server.pluginManager.callEvent(PrePlayerSwitchPlayerInstanceEvent(player, currentPlayerInstance, playerInstance))

		currentInstancesTwo.remove(currentPlayerInstance)
		currentInstances[player.uniqueId] = playerInstance
		currentInstancesTwo[playerInstance] = player.uniqueId

		server.pluginManager.callEvent(PostPlayerSwitchPlayerInstanceEvent(player, currentPlayerInstance, playerInstance))
	}

	override fun stopTracking(player: Player) {
		val currentPlayerInstance = this.getCurrentForPlayer(player) ?: return

		server.pluginManager.callEvent(PrePlayerSwitchPlayerInstanceEvent(player, currentPlayerInstance, null))

		val result = currentInstances.remove(player.uniqueId)
		currentInstancesTwo.remove(result)

		server.pluginManager.callEvent(PostPlayerSwitchPlayerInstanceEvent(player, currentPlayerInstance, null))
	}

	override fun getCurrentForPlayerInstance(instance: PlayerInstance): Player? {
		return currentInstancesTwo[instance]?.let { server.getPlayer(it) }
	}
}
