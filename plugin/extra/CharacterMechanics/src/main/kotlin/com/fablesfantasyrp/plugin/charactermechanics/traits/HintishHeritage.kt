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
package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.flaunch
import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.github.shynixn.mccoroutine.bukkit.launch
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
			flaunch {
				if (hasTrait(e.entity.killer!!)) {
					e.drops.forEach { it.amount *= 2 }
				}
			}
		}
	}
}
