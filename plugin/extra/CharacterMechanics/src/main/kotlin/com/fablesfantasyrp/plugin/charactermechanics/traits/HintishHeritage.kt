package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.entity.Cow
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.plugin.Plugin

// Players with this trait harvest double the meat and crops from their farms/animals due to their farmer's background.
class HintishHeritage(plugin: Plugin,
					  characters: CharacterRepository,
					  profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.HINTISH_HERITAGE, plugin, characters, profileManager) {

	override fun init() {
		super.init()
		server.pluginManager.registerEvents(HintishHeritageListener(), plugin)
	}

	inner class HintishHeritageListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerKillAnimal(e: EntityDeathEvent) {
			if (e.entity !is Cow) return
			if (e.entity.killer == null) return

			if (hasTrait(e.entity.killer!!)) {
				e.drops.forEach { it.amount *= 2 }
			}
		}
	}
}
