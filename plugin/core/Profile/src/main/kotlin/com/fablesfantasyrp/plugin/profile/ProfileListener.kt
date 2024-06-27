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
package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.domain.SPAWN
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.event.PlayerForceProfileSelectionEvent
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.TransactionStep
import kotlinx.coroutines.CancellationException
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.LOW
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class ProfileListener(private val plugin: JavaPlugin,
					  private val profiles: EntityProfileRepository,
					  private val profileManager: ProfileManager) : Listener {
	private val server get() = plugin.server
	private val playersCurrentlySwitchingProfile: MutableSet<Player> = HashSet()

	private suspend fun forceProfileSelection(player: Player, ownedProfiles: Collection<Profile>) {
		profileManager.stopTracking(player) // Just to be safe
		player.teleport(SPAWN)
		player.inventory.clear()
		player.enderChest.clear()
		require(profileManager.getCurrentForPlayer(player) == null)

		do {
			val selector = server.servicesManager.getRegistration(ProfilePrompter::class.java)!!.provider
			try {
				if (!player.isOnline || server.isStopping) return
				val newProfile = selector.promptSelect(player, ownedProfiles)
				profileManager.setCurrentForPlayer(player, newProfile)
				player.sendMessage("$SYSPREFIX You are now profile #${newProfile.id}")
			} catch (ex: ProfileSwitchException) {
				player.sendError(ex.message ?: "An unexpected error occurred during profile switch. Please try another.")
			} catch (_: CancellationException) {}
		} while (profileManager.getCurrentForPlayer(player) == null)
		require(profileManager.getCurrentForPlayer(player) != null)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		if (!e.player.isWhitelisted) return // TODO leaky abstraction from whitelist plugin

		val ownedProfiles = profiles.activeForOwner(e.player)
		if (ownedProfiles.isEmpty()) return

		if (!PlayerForceProfileSelectionEvent(e.player).callEvent()) return

		server.scheduler.scheduleSyncDelayedTask(plugin, {
			val player = server.getPlayer(e.player.uniqueId) ?: return@scheduleSyncDelayedTask
			flaunch { forceProfileSelection(player, ownedProfiles) }
		}, 1)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onProfileSwitch(e: PostPlayerSwitchProfileEvent) {
		if (e.new == null && e.old != null && PlayerForceProfileSelectionEvent(e.player).callEvent()) {
			val ownedProfiles = profiles.activeForOwner(e.player)
			flaunch { forceProfileSelection(e.player, ownedProfiles) }
		}
		playersCurrentlySwitchingProfile.remove(e.player)
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPreProfileSwitch(e: PlayerSwitchProfileEvent) {
		e.transaction.steps.add(TransactionStep(
			{ playersCurrentlySwitchingProfile.add(e.player) },
			{ playersCurrentlySwitchingProfile.remove(e.player) }
		))
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		profileManager.stopTracking(e.player)
		playersCurrentlySwitchingProfile.remove(e.player)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerMove(e: PlayerMoveEvent) {
		if (e.player.isWhitelisted && !playersCurrentlySwitchingProfile.contains(e.player) &&
				profileManager.getCurrentForPlayer(e.player) == null) {
			e.isCancelled = true
		}
	}
}
