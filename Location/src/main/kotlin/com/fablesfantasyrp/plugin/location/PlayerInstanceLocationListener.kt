package com.fablesfantasyrp.plugin.location

import com.fablesfantasyrp.plugin.location.data.entity.EntityPlayerInstanceLocationRepository
import com.fablesfantasyrp.plugin.playerinstance.event.PrePlayerSwitchPlayerInstanceEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class PlayerInstanceLocationListener(
		private val playerInstanceLocationRepository: EntityPlayerInstanceLocationRepository<*>) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchPlayerInstance(e: PrePlayerSwitchPlayerInstanceEvent) {
		val old = e.old
		val new = e.new

		if (old != null) playerInstanceLocationRepository.forOwner(old).player = null
		if (new != null) playerInstanceLocationRepository.forOwner(new).player = e.player
	}
}
