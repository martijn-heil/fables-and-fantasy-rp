package com.fablesfantasyrp.plugin.knockout

import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerDataEntity
import com.fablesfantasyrp.plugin.utils.isRealPlayer
import dev.geco.gsit.api.event.PreEntityGetUpSitEvent
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.LOW
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.*

class KnockoutListener(private val server: Server) : Listener {
	@EventHandler(ignoreCancelled = true)
	fun onPlayerDamageByEntity(e: EntityDamageByEntityEvent) {
		val player = e.entity as? Player ?: return
		if (!player.isRealPlayer) return

		val knockoutEntity = player.knockout

		if (knockoutEntity.isKnockedOut) {
			knockoutEntity.execute(e.cause, e.damager)
			e.isCancelled = true
		} else if (player.health - e.finalDamage <= 0) {
			knockoutEntity.knockout(e.cause, e.damager)
			e.isCancelled = true
		}
	}

	@EventHandler(ignoreCancelled = true)
	fun onPlayerDamageByBlock(e: EntityDamageByBlockEvent) {
		val player = e.entity as? Player ?: return
		if (!player.isRealPlayer) return

		val knockoutEntity = player.knockout

		if (knockoutEntity.isKnockedOut) {
			knockoutEntity.execute(e.cause, null)
			e.isCancelled = true
		} else if (player.health - e.finalDamage <= 0) {
			knockoutEntity.knockout(e.cause, null)
			e.isCancelled = true
		}
	}

	@EventHandler(ignoreCancelled = true)
	fun onPlayerDamage(e: EntityDamageEvent) {
		if (e is EntityDamageByBlockEvent || e is EntityDamageByEntityEvent) return
		val player = e.entity as? Player ?: return
		if (!player.isRealPlayer) return

		val knockoutEntity = player.knockout

		if (knockoutEntity.isKnockedOut) {
			knockoutEntity.execute(e.cause, null)
			e.isCancelled = true
		} else if (player.health - e.finalDamage <= 0) {
			knockoutEntity.knockout(e.cause, null)
			e.isCancelled = true
		}
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val knockoutEntity = e.player.knockout as? KnockoutPlayerDataEntity ?: return
		knockoutEntity.tick()
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerRightClick(e: PlayerInteractAtEntityEvent) {
		val target = e.rightClicked as? Player ?: return
		if (!target.isRealPlayer) return
		val player = e.player
		val knockoutEntity = target.knockout as? KnockoutPlayerDataEntity ?: return
		if (knockoutEntity.isKnockedOut && !knockoutEntity.isBeingRevived) {
			knockoutEntity.startDelayedRevival(player)
		}
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerDeath(e: PlayerDeathEvent) {
		val knockoutEntity = e.player.knockout
		if (knockoutEntity.isKnockedOut) knockoutEntity.execute(null, null)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		val knockoutEntity = e.player.knockout
		if (knockoutEntity.isKnockedOut) knockoutEntity.execute(null, null)
	}

	@EventHandler(ignoreCancelled = true)
	fun onPlayerRegainHealth(e: EntityRegainHealthEvent) {
		val player = e.entity as? Player ?: return
		val knockoutEntity = player.knockout
		if (knockoutEntity.isKnockedOut) e.isCancelled = true
	}

	@EventHandler(ignoreCancelled = true)
	fun onPlayerGetUp(e: PreEntityGetUpSitEvent) {
		val player = e.entity as? Player ?: return
		if (player.knockout.isKnockedOut) e.isCancelled = true
	}

	@EventHandler(ignoreCancelled = true)
	fun onPlayerInteract(e: PlayerInteractEvent) {
		val knockoutEntity = e.player.knockout
		if (knockoutEntity.isKnockedOut) e.isCancelled = true
	}

	@EventHandler(ignoreCancelled = true)
	fun onPlayerInteractAtEntity(e: PlayerInteractAtEntityEvent) {
		val knockoutEntity = e.player.knockout
		if (knockoutEntity.isKnockedOut) e.isCancelled = true
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerDamageByPlayer(e: EntityDamageByEntityEvent) {
		val damager = e.entity as? Player ?: return
		if (!damager.isRealPlayer) return

		if (damager.knockout.isKnockedOut) e.isCancelled = true
	}

	@EventHandler(ignoreCancelled = true)
	fun onPlayerCommandPreprocessEvent(e: PlayerCommandPreprocessEvent) {
		val knockoutEntity = e.player.knockout
		if (knockoutEntity.isKnockedOut) e.isCancelled = true
	}
}
