package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.plugin.Plugin

class DisableCraftingRecipes(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	private val materials = hashSetOf(Material.LODESTONE)

	override fun init() {
		server.pluginManager.registerEvents(DisableCraftingRecipesListener(), plugin)
	}

	inner class DisableCraftingRecipesListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onCraftItem(e: CraftItemEvent) {
			if (materials.contains(e.recipe.result.type)) {
				e.isCancelled = true
			}
		}
	}
}
