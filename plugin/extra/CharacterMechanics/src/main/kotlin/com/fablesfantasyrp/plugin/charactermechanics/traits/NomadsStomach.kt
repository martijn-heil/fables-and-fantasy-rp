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
import com.fablesfantasyrp.plugin.charactermechanics.frunBlocking
import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import com.github.shynixn.mccoroutine.bukkit.launch
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
			frunBlocking {
				val player = e.entity as? Player ?: return@frunBlocking
				if (e.foodLevel > player.foodLevel) return@frunBlocking
				val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return@frunBlocking

				if (odd.contains(character)) {
					e.isCancelled = true
					odd.remove(character)
				} else {
					odd.add(character)
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerProfileChange(e: PostPlayerSwitchProfileEvent) {
			flaunch {
				val character = e.old?.let { characters.forProfile(it) } ?: return@flaunch
				odd.remove(character)
			}
		}
	}
}
