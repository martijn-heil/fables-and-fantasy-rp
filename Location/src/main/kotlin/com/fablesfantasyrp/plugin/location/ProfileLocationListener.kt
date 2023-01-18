package com.fablesfantasyrp.plugin.location

import com.fablesfantasyrp.plugin.location.data.entity.EntityProfileLocationRepository
import com.fablesfantasyrp.plugin.profile.event.PrePlayerSwitchProfileEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ProfileLocationListener(
		private val profileLocationRepository: EntityProfileLocationRepository<*>) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchProfile(e: PrePlayerSwitchProfileEvent) {
		val old = e.old
		val new = e.new

		if (old != null) profileLocationRepository.forOwner(old).player = null
		if (new != null) profileLocationRepository.forOwner(new).player = e.player
	}
}
