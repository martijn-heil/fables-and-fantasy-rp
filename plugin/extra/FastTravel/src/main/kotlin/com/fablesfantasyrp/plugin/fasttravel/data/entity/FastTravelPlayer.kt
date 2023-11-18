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
