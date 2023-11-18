package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.plugin.Plugin

class NoTotemOfUndying(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(NoTotemOfUndyingListener(), plugin)
	}

	inner class NoTotemOfUndyingListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerResurrect(e: EntityResurrectEvent) {
			e.isCancelled = true
		}
	}
}
