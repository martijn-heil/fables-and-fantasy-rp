package com.fablesfantasyrp.plugin.staffplayerinstances

import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.staffplayerinstances.data.StaffPlayerInstanceRepository
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class StaffPlayerInstancesListener(private val plugin: Plugin,
								   private val playerInstances: EntityPlayerInstanceRepository,
								   private val staffPlayerInstances: StaffPlayerInstanceRepository,
								   private val worldBoundPlayerInstancesHook: WorldBoundPlayerInstancesHook?) : Listener {
	private val server = plugin.server

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		if (!e.player.hasPermission("fables.staffplayerinstances.staff")) return

		val ownedPlayerInstances = playerInstances.forOwner(e.player).filter { it.isActive }
		if (staffPlayerInstances.containsAny(ownedPlayerInstances)) return

		val newPlayerInstance = playerInstances.create(PlayerInstance(
				owner = e.player,
				description = "Staff",
				isActive = true
		))

		staffPlayerInstances.create(newPlayerInstance)

		worldBoundPlayerInstancesHook?.allowToFlatroom(newPlayerInstance)

		plugin.logger.info("Created staff player instance #${newPlayerInstance.id} for ${e.player.name}")
	}
}
