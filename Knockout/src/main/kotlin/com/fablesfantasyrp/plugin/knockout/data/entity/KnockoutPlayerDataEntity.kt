package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.knockout.PLUGIN
import com.fablesfantasyrp.plugin.knockout.data.KnockoutPlayerData
import com.fablesfantasyrp.plugin.knockout.data.KnockoutState
import com.fablesfantasyrp.plugin.knockout.getFirstBlockBelowLocation
import com.github.shynixn.mccoroutine.launch
import dev.geco.gsit.api.GSitAPI
import dev.geco.gsit.objects.GetUpReason
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
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

	var revivalJob: Job? = null
	var executionJob: Job? = null
	var helper: Player? = null
	val isBeingRevived: Boolean
		get() = helper != null

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
		this.startDelayedExecution()
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
		this.executionJob?.cancel()
		this.executionJob = null
		this.revivalJob?.cancel()
		this.revivalJob = null
		this.helper = null

		this.knockedOutAt = null
		this.knockoutCause = null
		this.knockoutDamager = null
		this.state = KnockoutState.REVIVED
		if (this.offlinePlayer.isOnline) this.tick()
	}

	override fun execute(cause: EntityDamageEvent.DamageCause?, by: Entity?) {
		check(this.state == KnockoutState.KNOCKED_OUT)
		this.revivalJob?.cancel()
		this.revivalJob = null
		this.helper = null
		this.executionJob?.cancel()
		this.executionJob = null

		this.knockedOutAt = null
		this.knockoutDamager = null
		this.knockoutCause = null
		this.state = KnockoutState.EXECUTED
		if (this.offlinePlayer.isOnline) this.tick()
	}

	fun startDelayedRevival(helper: Player) {
		check(this.revivalJob == null)
		check(this.state == KnockoutState.KNOCKED_OUT)

		this.helper = helper
		val helperLocation = helper.location

		this.revivalJob = PLUGIN.launch {
			try {
				for (i in 5 downTo 1) {
					helper.showTitle(Title.title(Component.text(i).color(NamedTextColor.YELLOW), Component.empty()))
					delay(1000)

					if (!helper.isOnline || !offlinePlayer.isOnline) {
						this@KnockoutPlayerDataEntity.helper = null
						revivalJob = null
						helper.showTitle(Title.title(Component.text("x").color(NamedTextColor.RED), Component.empty()))
						return@launch
					}

					if (helper.location.world != helperLocation.world || helper.location.distance(helperLocation) > 0.5) {
						helper.showTitle(Title.title(Component.text("x").color(NamedTextColor.RED), Component.empty()))
						this@KnockoutPlayerDataEntity.helper = null
						revivalJob = null
						return@launch
					}
				}

				helper.clearTitle()
				revive(helper)
			} catch (ex: CancellationException) {
				helper.clearTitle()
			}
		}
	}

	fun startDelayedExecution() {
		check(this.executionJob == null)
		check(this.state == KnockoutState.KNOCKED_OUT)

		this.executionJob = PLUGIN.launch {
			try {
				var i = 30

				while (i > 0) {
					val player = offlinePlayer.player
					if (offlinePlayer.isOnline && player != null) {
						player.showTitle(Title.title(Component.text(i).color(NamedTextColor.YELLOW), Component.empty()))
					}

					if (!isBeingRevived) {
						i--
					}
					delay(1000)
				}

				offlinePlayer.player?.clearTitle()
				execute(null, null)
			} catch(ex: CancellationException) {
				offlinePlayer.player?.clearTitle()
			}
		}
	}


	override fun equals(other: Any?): Boolean = other is KnockoutPlayerData && other.id == this.id
	override fun hashCode(): Int = this.id.hashCode()
}
