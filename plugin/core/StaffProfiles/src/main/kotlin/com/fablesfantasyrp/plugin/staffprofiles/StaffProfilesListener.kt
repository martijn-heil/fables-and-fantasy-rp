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
package com.fablesfantasyrp.plugin.staffprofiles

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.staffprofiles.domain.repository.StaffProfileRepository
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class StaffProfilesListener(private val plugin: Plugin,
							private val profiles: EntityProfileRepository,
							private val profileManager: ProfileManager,
							private val staffProfiles: StaffProfileRepository,
							private val worldBoundProfilesHook: WorldBoundProfilesHook?) : Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		if (!e.player.hasPermission("fables.staffprofiles.staff")) return

		val ownProfiles = profiles.activeForOwner(e.player)
		if (frunBlocking { staffProfiles.containsAny(ownProfiles) }) return

		val newProfile = profiles.create(Profile(
				owner = e.player,
				description = "Staff",
				isActive = true
		))

		flaunch {
			staffProfiles.create(newProfile)
			worldBoundProfilesHook?.allowToFlatroom(newProfile)
			plugin.logger.info("Created staff profile #${newProfile.id} for ${e.player.name}")
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onFoodLevelChange(e: FoodLevelChangeEvent) {
		val player = e.entity as? Player ?: return
		val profile = profileManager.getCurrentForPlayer(player) ?: return
		if (!frunBlocking { staffProfiles.contains(profile) }) return

		if (e.foodLevel < player.foodLevel) {
			e.isCancelled = true
		}
	}
}
