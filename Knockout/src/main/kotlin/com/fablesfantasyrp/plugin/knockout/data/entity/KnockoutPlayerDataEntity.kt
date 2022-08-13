package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.knockout.data.KnockoutPlayerData
import com.fablesfantasyrp.plugin.knockout.getFirstBlockBelowLocation
import dev.geco.gsit.api.GSitAPI
import dev.geco.gsit.objects.GetUpReason
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import java.time.Instant
import java.util.*

class KnockoutPlayerDataEntity : KnockoutPlayerEntity, HasDirtyMarker<KnockoutPlayerEntity> {

	private val offlinePlayer: OfflinePlayer get() = Bukkit.getOfflinePlayer(id)

	override var knockedOutAt: Instant? = null
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var knockoutCause: EntityDamageEvent.DamageCause? = null
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var knockoutDamager: Entity? = null
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val id: UUID

	constructor(id: UUID, knockedOutAt: Instant?, knockoutCause: EntityDamageEvent.DamageCause?, knockoutDamager: Entity?) {
		this.id = id
		this.knockedOutAt = knockedOutAt
		this.knockoutCause = knockoutCause
		this.knockoutDamager = knockoutDamager
	}

	override var dirtyMarker: DirtyMarker<KnockoutPlayerEntity>? = null

	override fun knockout(cause: EntityDamageEvent.DamageCause?, by: Entity?) {
		this.knockoutCause = cause
		this.knockoutDamager = by
		this.knockedOutAt = Instant.now()
		if (this.offlinePlayer.isOnline) this.applyKnockoutEffects()
	}

	fun applyKnockoutEffects() {
		if (this.offlinePlayer.player == null) throw UnsupportedOperationException("Player is not online")
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")
		val block = getFirstBlockBelowLocation(player.location)
		val gsit = GSitAPI.getInstance()
		gsit.sitManager.createSeat(block, player)
	}

	override fun revive(by: Player?) {
		this.knockedOutAt = null
		this.knockoutCause = null
		this.knockoutDamager = null
		val player = this.offlinePlayer.player
		if (this.offlinePlayer.isOnline && player != null) {
			GSitAPI.getInstance().sitManager.removeSeat(player, GetUpReason.PLUGIN)
		}
	}

	override fun execute(cause: EntityDamageEvent.DamageCause?, by: Entity?) {
		if (!this.offlinePlayer.isOnline) throw UnsupportedOperationException("Player is not online")
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")

		player.health = 0.0
	}


	override fun equals(other: Any?): Boolean = other is KnockoutPlayerData && other.id == this.id
	override fun hashCode(): Int = this.id.hashCode()
}
