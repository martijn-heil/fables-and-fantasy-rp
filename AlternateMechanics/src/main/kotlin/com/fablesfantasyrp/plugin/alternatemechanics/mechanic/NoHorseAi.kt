package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityUnleashEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.plugin.Plugin

class NoHorseAi(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(NoHorseAiListener(), plugin)
	}

	inner class NoHorseAiListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerLeashEntity(e: PlayerLeashEntityEvent) {
			val entity = e.entity as? LivingEntity ?: return
			if (entity.type == EntityType.HORSE ||
				entity.type == EntityType.SKELETON_HORSE ||
				entity.type == EntityType.SKELETON_HORSE ||
				entity.type == EntityType.DONKEY) {
				entity.setAI(true)
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerBreaksLead(e: EntityUnleashEvent) {
			val entity = e.entity as? LivingEntity ?: return
			if (entity.type == EntityType.HORSE ||
				entity.type == EntityType.SKELETON_HORSE ||
				entity.type == EntityType.SKELETON_HORSE ||
				entity.type == EntityType.DONKEY) {
				entity.setAI(false)
			}
		}
	}
}
