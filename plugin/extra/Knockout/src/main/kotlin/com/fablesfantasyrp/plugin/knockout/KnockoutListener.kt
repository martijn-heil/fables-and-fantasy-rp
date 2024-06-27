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
package com.fablesfantasyrp.plugin.knockout

import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerDataEntity
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isRealPlayer
import dev.geco.gsit.api.event.PreEntityGetUpSitEvent
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.*
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

	@EventHandler(priority = NORMAL, ignoreCancelled = true)
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
		val damager = e.damager as? Player ?: return
		if (!damager.isRealPlayer) return

		if (damager.knockout.isKnockedOut) e.isCancelled = true
	}

	@EventHandler(ignoreCancelled = true)
	fun onPlayerCommandPreprocessEvent(e: PlayerCommandPreprocessEvent) {
		val knockoutEntity = e.player.knockout
		if (knockoutEntity.isKnockedOut) e.isCancelled = true
	}
}
