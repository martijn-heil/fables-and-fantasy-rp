package com.fablesfantasyrp.plugin.alternatemechanics

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.event.entity.EntityUnleashEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.spigotmc.event.entity.EntityMountEvent

class AlternateMechanicsListener : Listener {
	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerItemConsume(e: PlayerItemConsumeEvent) {
		if (setOf(Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE).contains(e.item.type)) {
			e.isCancelled = true
		}
	}

	// Cancel totem of undying effects
	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerResurrect(e: EntityResurrectEvent) {
		e.isCancelled = true
	}

	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerLeashEntity(e: PlayerLeashEntityEvent) {
		if (e.player.isInsideVehicle) {
			e.player.sendRichMessage("<red>You can't leash entities while in a vehicle.")
			e.isCancelled = true
		}
	}

	// Don't allow players to move animals by leashing them while in a vehicle, like a boat.
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerEnterVehicle(e: VehicleEnterEvent) {
		val player = e.entered as? Player ?: return
		val location = player.location
		location.getNearbyLivingEntities(30.0)
			.filter { it.isLeashed && it.leashHolder == player }
			.forEach {
				it.setLeashHolder(null)
				location.world.playSound(location, Sound.ENTITY_LEASH_KNOT_BREAK, 1f, 1f)
			}
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerBreaksLead(event: EntityUnleashEvent) {
		if (event.reason == EntityUnleashEvent.UnleashReason.DISTANCE ||
			event.reason == EntityUnleashEvent.UnleashReason.HOLDER_GONE) {
			val location = event.entity.location
			val world = location.world
			world.playSound(location, Sound.ENTITY_LEASH_KNOT_BREAK, 1f, 1f)
		}
	}

	val MAX_JUMP_STRENGTH = 0.6
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerMountHorse(e: EntityMountEvent) {
		val horse = e.mount as? Horse ?: return
		if (horse.jumpStrength > MAX_JUMP_STRENGTH) {
			horse.jumpStrength = MAX_JUMP_STRENGTH
		}

		horse.setAI(false)
	}
}
