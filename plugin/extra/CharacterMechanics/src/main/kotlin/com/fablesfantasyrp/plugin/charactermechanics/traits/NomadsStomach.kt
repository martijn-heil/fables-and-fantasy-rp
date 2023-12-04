package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.plugin.Plugin

// The food bar of players with this trait lowers significantly slower than usual.
class NomadsStomach(plugin: Plugin,
					characters: CharacterRepository,
					profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.NOMADS_STOMACH, plugin, characters, profileManager) {
	private val odd = HashSet<Character>()

	override fun init() {
		super.init()

		server.pluginManager.registerEvents(NomadsStomachListener(), plugin)
	}

	inner class NomadsStomachListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onFoodLevelChange(e: FoodLevelChangeEvent) {
			val player = e.entity as? Player ?: return
			if (e.foodLevel > player.foodLevel) return
			val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return

			if (odd.contains(character)) {
				e.isCancelled = true
				odd.remove(character)
			} else {
				odd.add(character)
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerProfileChange(e: PostPlayerSwitchProfileEvent) {
			val character = e.old?.let { characters.forProfile(it) } ?: return
			odd.remove(character)
		}
	}
}
