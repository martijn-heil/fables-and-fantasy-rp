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
package com.fablesfantasyrp.plugin.fasttravel.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.fasttravel.PLUGIN
import com.fablesfantasyrp.plugin.fasttravel.data.FastTravelLinkData
import com.fablesfantasyrp.plugin.timers.countdown
import com.fablesfantasyrp.plugin.worldguardinterop.toBlockVector3
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

class FastTravelPlayer : DataEntity<UUID, FastTravelPlayer> {
	override var dirtyMarker: DirtyMarker<FastTravelPlayer>? = null
	override val id: UUID
	private val offlinePlayer: OfflinePlayer get() = PLUGIN.server.getOfflinePlayer(id)
	private val onlinePlayer: Player? get() = offlinePlayer.player

	constructor(id: UUID, dirtyMarker: DirtyMarker<FastTravelPlayer>? = null) {
		this.id = id
		this.dirtyMarker = dirtyMarker
	}

	private var fastTravelTask: Job? = null
	val isFastTravelling get() = fastTravelTask != null && !fastTravelTask!!.isCompleted

	fun cancelFastTravel() {
		fastTravelTask?.cancel()
		fastTravelTask = null
	}

	fun useLink(link: FastTravelLinkData) {
		val player = onlinePlayer
		check(offlinePlayer.isOnline && player != null)

		fastTravelTask = PLUGIN.launch {
			delay(1)
			player.countdown(
					duration = link.travelDuration.inWholeSeconds.toUInt(),
					preventions = emptyList(),
					cancelReasons = emptyList(),
					actionBar = Component.text("Travelling to your destination.."),
					shouldCancel = { link.from.world != player.world || !link.from.region.contains(player.location.toBlockVector3()) }
			)
			player.teleport(link.to)
		}
	}
}
