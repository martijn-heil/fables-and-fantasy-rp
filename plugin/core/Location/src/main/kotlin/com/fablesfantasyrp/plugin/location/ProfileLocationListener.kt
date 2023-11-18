package com.fablesfantasyrp.plugin.location

import com.fablesfantasyrp.plugin.location.data.entity.EntityProfileLocationRepository
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ProfileLocationListener(private val profileLocationRepository: EntityProfileLocationRepository<*>) : Listener {
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	fun onPlayerSwitchProfile(e: PlayerSwitchProfileEvent) {
		val old = e.old
		val new = e.new
		val transaction = e.transaction

		if (old != null) transaction.setProperty(profileLocationRepository.forOwner(old)::player, null)
		if (new != null) transaction.setProperty(profileLocationRepository.forOwner(new)::player, e.player)
	}
}
