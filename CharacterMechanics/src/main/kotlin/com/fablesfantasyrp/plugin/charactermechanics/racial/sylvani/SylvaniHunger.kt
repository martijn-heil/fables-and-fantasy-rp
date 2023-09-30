package com.fablesfantasyrp.plugin.charactermechanics.racial.sylvani

import com.fablesfantasyrp.plugin.charactermechanics.racial.base.BaseRaceBehavior
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.plugin.Plugin

// Players with this trait get two extra points in the strength stat.
class SylvaniHunger(plugin: Plugin,
					characters: CharacterRepository,
					profileManager: ProfileManager)
	: BaseRaceBehavior(Race.SYLVANI, plugin, characters, profileManager) {

	override fun init() {
		super.init()
		server.pluginManager.registerEvents(SylvaniHungerListener(), plugin)
	}

	inner class SylvaniHungerListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onFoodLevelChange(e: FoodLevelChangeEvent) {
			if (e.foodLevel >= e.entity.foodLevel) return
			val player = e.entity as? Player ?: return
			val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return

			if (character.race == Race.SYLVANI) {
				e.isCancelled = true
			}
		}
	}
}
