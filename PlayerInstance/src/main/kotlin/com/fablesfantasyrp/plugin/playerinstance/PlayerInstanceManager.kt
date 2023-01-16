package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.entity.Player
import java.util.*

class PlayerInstanceOccupiedException(val by: Player) : Exception()

interface PlayerInstanceManager {
	@Throws(PlayerInstanceOccupiedException::class)
	fun setCurrentForPlayer(player: Player, playerInstance: PlayerInstance, force: Boolean = false)
	fun getCurrentForPlayer(player: Player): PlayerInstance?
	fun stopTracking(player: Player)
	fun getCurrentForPlayerInstance(instance: PlayerInstance): Player?
}
