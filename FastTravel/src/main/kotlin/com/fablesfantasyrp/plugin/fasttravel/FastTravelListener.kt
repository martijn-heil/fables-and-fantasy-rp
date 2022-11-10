package com.fablesfantasyrp.plugin.fasttravel

import com.fablesfantasyrp.plugin.fasttravel.data.entity.FastTravelLinkRepository
import com.fablesfantasyrp.plugin.timers.CountdownCancelledEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class FastTravelListener(private val links: FastTravelLinkRepository) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerMove(e: PlayerMoveEvent) {
		val player = e.player
		val location = player.location
		val fastTravelPlayer = player.fastTravel
		if (fastTravelPlayer.isFastTravelling) return

		val link = links.all().find { it.from.region.contains(location.toBlockVector3()) } ?: return
		fastTravelPlayer.useLink(link)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onCountdownCancelled(e: CountdownCancelledEvent) {
		e.player.fastTravel.cancelFastTravel()
	}
}
