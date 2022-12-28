package com.fablesfantasyrp.plugin.basicsystem

import com.fablesfantasyrp.plugin.basicsystem.data.entity.EntityBasicSystemPlayerRepository
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class BasicSystemListener(private val players: EntityBasicSystemPlayerRepository) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val basicSystemPlayer = players.forPlayer(e.player)
		if (!basicSystemPlayer.isEpic) {
			e.player.sendMessage("$SYSPREFIX ayo! you are not so epic!")
		}
	}
}
