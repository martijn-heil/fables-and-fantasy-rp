package com.fablesfantasyrp.plugin.knockout

import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent

class KnockoutListener(private val server: Server) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerDamageByEntity(e: EntityDamageByEntityEvent) {
		if (e.finalDamage >= 0) return
		val player = e.entity as? Player ?: return
		val knockoutEntity = player.knockout
		e.isCancelled = true

		if (knockoutEntity.isKnockedOut) {
			knockoutEntity.execute(e.cause, e.damager)
		} else {
			knockoutEntity.knockout(e.cause, e.damager)
		}

		player.knockout.knockout(e.cause, e.damager)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerDamageByBlock(e: EntityDamageByBlockEvent) {
		if (e.finalDamage >= 0) return
		val player = e.entity as? Player ?: return
		val knockoutEntity = player.knockout
		e.isCancelled = true

		if (knockoutEntity.isKnockedOut) {
			knockoutEntity.execute(e.cause, null)
		} else {
			knockoutEntity.knockout(e.cause, null)
		}
	}
}
