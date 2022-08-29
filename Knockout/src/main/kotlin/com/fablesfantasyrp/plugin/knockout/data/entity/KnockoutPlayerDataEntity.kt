package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.knockout.data.KnockoutPlayerData
import com.fablesfantasyrp.plugin.knockout.data.KnockoutState
import com.fablesfantasyrp.plugin.knockout.getFirstBlockBelowLocation
import dev.geco.gsit.api.GSitAPI
import dev.geco.gsit.objects.GetUpReason
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Instant
import java.util.*

class KnockoutPlayerDataEntity : KnockoutPlayerEntity, HasDirtyMarker<KnockoutPlayerEntity> {

	private val offlinePlayer: OfflinePlayer get() = Bukkit.getOfflinePlayer(id)

	override var state: KnockoutState? = null
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var knockedOutAt: Instant? = null
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var knockoutCause: EntityDamageEvent.DamageCause? = null
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var knockoutDamager: Entity? = null
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val id: UUID

	constructor(id: UUID, state: KnockoutState?, knockedOutAt: Instant?, knockoutCause: EntityDamageEvent.DamageCause?, knockoutDamager: Entity?) {
		this.id = id
		this.state = state
		this.knockedOutAt = knockedOutAt
		this.knockoutCause = knockoutCause
		this.knockoutDamager = knockoutDamager
	}

	override var dirtyMarker: DirtyMarker<KnockoutPlayerEntity>? = null

	override fun knockout(cause: EntityDamageEvent.DamageCause?, by: Entity?) {
		this.knockoutCause = cause
		this.knockoutDamager = by
		this.knockedOutAt = Instant.now()
		this.state = KnockoutState.KNOCKED_OUT
		if (this.offlinePlayer.isOnline) this.tick()
		this.offlinePlayer.player?.sendMessage("You have been knocked out!")
	}

	fun applyKnockoutEffects() {
		if (this.offlinePlayer.player == null) throw UnsupportedOperationException("Player is not online")
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")
		val block = getFirstBlockBelowLocation(player.location)
		val gsit = GSitAPI.getInstance()
		gsit.sitManager.createSeat(block, player)
		player.health = 1.0
		player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 999999, 1, false, false))
	}

	fun removeKnockOutEffects() {
		val player = this.offlinePlayer.player
		if (!this.offlinePlayer.isOnline || player == null) return

		GSitAPI.getInstance().sitManager.removeSeat(player, GetUpReason.PLUGIN)
		player.removePotionEffect(PotionEffectType.BLINDNESS)
	}

	fun tick() {
		if (!this.offlinePlayer.isOnline) throw UnsupportedOperationException("Player is not online")
		val player = this.offlinePlayer.player ?: throw UnsupportedOperationException("Player is not online")

		when (state) {
			null -> return

			KnockoutState.KNOCKED_OUT -> {
				this.applyKnockoutEffects()
			}

			KnockoutState.EXECUTED -> {
				this.removeKnockOutEffects()
				this.state = null
				player.health = 0.0
			}

			KnockoutState.REVIVED -> {
				this.removeKnockOutEffects()
				player.health = 6.0
				this.state = null
			}
		}
	}

	override fun revive(by: Player?) {
		check(this.state == KnockoutState.KNOCKED_OUT)

		this.knockedOutAt = null
		this.knockoutCause = null
		this.knockoutDamager = null
		this.state = KnockoutState.REVIVED
		if (this.offlinePlayer.isOnline) this.tick()
	}

	override fun execute(cause: EntityDamageEvent.DamageCause?, by: Entity?) {
		check(this.state == KnockoutState.KNOCKED_OUT)

		this.knockedOutAt = null
		this.knockoutDamager = null
		this.knockoutCause = null
		this.state = KnockoutState.EXECUTED
		if (this.offlinePlayer.isOnline) this.tick()
	}


	override fun equals(other: Any?): Boolean = other is KnockoutPlayerData && other.id == this.id
	override fun hashCode(): Int = this.id.hashCode()
}
