package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.knockout.data.KnockoutPlayerData
import com.fablesfantasyrp.plugin.knockout.getFirstBlockBelowLocation
import dev.geco.gsit.api.GSitAPI
import dev.geco.gsit.objects.GetUpReason
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.time.Instant
import java.util.*

class KnockoutPlayerDataEntity : KnockoutPlayerEntity, HasDirtyMarker<KnockoutPlayerEntity> {

	private val offlinePlayer: OfflinePlayer get() = Bukkit.getOfflinePlayer(id)

	override var knockedOutAt: Instant? = null
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val id: UUID

	constructor(id: UUID, knockedOutAt: Instant?) {
		this.id = id
		this.knockedOutAt = knockedOutAt
	}

	override var dirtyMarker: DirtyMarker<KnockoutPlayerEntity>? = null

	override fun knockout(by: Player?) {
		this.knockedOutAt = Instant.now()
		if (this.offlinePlayer.isOnline) this.applyKnockoutEffects()
	}

	fun applyKnockoutEffects() {
		if (this.offlinePlayer.player == null) throw UnsupportedOperationException("Player is not online")
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")
		val block = getFirstBlockBelowLocation(player.location)
		GSitAPI.getInstance().sitManager.createSeat(block, player)
	}

	override fun revive(by: Player?) {
		this.knockedOutAt = null
		val player = this.offlinePlayer.player
		if (this.offlinePlayer.isOnline && player != null) {
			GSitAPI.getInstance().sitManager.removeSeat(player, GetUpReason.PLUGIN)
		}
	}


	override fun equals(other: Any?): Boolean = other is KnockoutPlayerData && other.id == this.id
	override fun hashCode(): Int = this.id.hashCode()
}
