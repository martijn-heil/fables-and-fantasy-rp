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
