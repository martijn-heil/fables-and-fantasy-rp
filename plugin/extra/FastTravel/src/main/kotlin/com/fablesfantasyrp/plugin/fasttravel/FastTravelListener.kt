/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.fasttravel

import com.fablesfantasyrp.plugin.fasttravel.data.entity.FastTravelLinkRepository
import com.fablesfantasyrp.plugin.timers.CountdownCancelledEvent
import com.fablesfantasyrp.plugin.worldguardinterop.toBlockVector3
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
