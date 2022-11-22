package com.fablesfantasyrp.plugin.timers

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.player.*

private val countdowns = HashMap<Player, CountdownJob>()

var Player.currentCountdown: CountdownJob?
	get() = countdowns[this]
	private set(value) {
		if (value != null) {
			countdowns[this] = CountdownJob(emptyList(), emptyList(), value)
		} else {
			countdowns.remove(this)
		}
	}

class CountdownJob(val cancelReasons: Collection<CancelReason>,
							   val countdownPreventions: Collection<CountdownPrevention>, job: Job) : Job by job {
	fun maybeCancel(reason: CancelReason): Boolean {
		return if (cancelReasons.contains(reason)) {
			this.cancel()
			true
		} else false
	}
}

enum class CancelReason {
	MOVEMENT,
	HURT
}

enum class CountdownPrevention {
	MOVEMENT,
	CHAT,
	COMMAND,
}

class CountdownBusyException : IllegalStateException()

@Throws(CountdownBusyException::class)
suspend fun Player.countdown(duration: UInt,
							 preventions: Collection<CountdownPrevention>,
							 cancelReasons: Collection<CancelReason>,
							 overrideExistingCountdown: Boolean = false,
							 shouldCancel: () -> Boolean = { false },
							 actionBar: Component? = null,
							 endTitle: String = "\u2611"): Boolean {
	val future = CompletableDeferred<Boolean>()
	val currentJob = this.currentCountdown
	if (currentJob != null) {
		if (overrideExistingCountdown) {
			currentJob.cancel(CancellationException("New countdown started"))
		} else throw CountdownBusyException()
	}

	fun cancelEffect() {
		playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.0f)
		sendTitle("$RED" + "$BOLD" + "\u274C", "")
	}

	val job = CountdownJob(cancelReasons, preventions, PLUGIN.launch {
		try {
			for (i in duration downTo 1U) {
				if(!isOnline) throw CancellationException("Player left the game")

				if (shouldCancel()) {
					cancelEffect()
					future.complete(false)
					return@launch
				}

				playSound(location, Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 2.0f)
				sendTitle("$YELLOW" + "$BOLD" + "$i", "")
				if (actionBar != null) sendActionBar(actionBar)

				delay(1000)
			}

			sendTitle("$GREEN" + "$BOLD" + endTitle, "")
			future.complete(true)
		} catch (e: CancellationException) {
			cancelEffect()
			future.complete(false)
		}
	})
	this.currentCountdown = job

	val result = future.await()
	if (!result) Bukkit.getPluginManager().callEvent(CountdownCancelledEvent(this, job))
	this.currentCountdown = null
	if (!result) throw CancellationException() else return true
}

internal class CountdownListener : Listener {
	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerMove(e: PlayerMoveEvent) {
		val job = e.player.currentCountdown ?: return
		job.maybeCancel(CancelReason.MOVEMENT)
		if (job.countdownPreventions.contains(CountdownPrevention.MOVEMENT)) e.isCancelled = true
	}

	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerChat(e: PlayerChatEvent) {
		val job = e.player.currentCountdown ?: return
		if (job.countdownPreventions.contains(CountdownPrevention.CHAT)) e.isCancelled = true
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerDamage(e: EntityDamageByBlockEvent) {
		val player = e.entity as? Player ?: return

		val job = player.currentCountdown ?: return
		job.maybeCancel(CancelReason.HURT)
	}

	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerCommandPreProcess(e: PlayerCommandPreprocessEvent) {
		val job = e.player.currentCountdown ?: return
		if (job.countdownPreventions.contains(CountdownPrevention.COMMAND)) e.isCancelled = true
	}

	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		e.player.currentCountdown?.cancel(CancellationException("Player left the game"))
	}
}

class CountdownCancelledEvent(player: Player, val job: CountdownJob) : PlayerEvent(player) {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
