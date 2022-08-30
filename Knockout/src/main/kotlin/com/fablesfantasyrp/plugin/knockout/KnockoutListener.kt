package com.fablesfantasyrp.plugin.knockout

import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerDataEntity
import dev.geco.gsit.api.event.PreEntityGetUpSitEvent
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent

class KnockoutListener(private val server: Server) : Listener {
	@EventHandler(ignoreCancelled = true)
	fun onPlayerDamageByEntity(e: EntityDamageByEntityEvent) {
		val player = e.entity as? Player ?: return

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
	fun onPlayerDamageByBlock(e: EntityDamageByBlockEvent) {
		val player = e.entity as? Player ?: return

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
		val player = e.entity as? Player ?: return

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

//	@EventHandler(priority = MONITOR, ignoreCancelled = true)
//	fun onPlayerQuit(e: PlayerQuitEvent) {
//		val knockoutEntity = e.player.knockout
//		if (knockoutEntity.isKnockedOut) knockoutEntity.execute(EntityDamageEvent.DamageCause.CUSTOM, null)
//	}

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

	@EventHandler(ignoreCancelled = true)
	fun onPlayerCommandPreprocessEvent(e: PlayerCommandPreprocessEvent) {
		val knockoutEntity = e.player.knockout
		if (knockoutEntity.isKnockedOut) e.isCancelled = true
	}
}
