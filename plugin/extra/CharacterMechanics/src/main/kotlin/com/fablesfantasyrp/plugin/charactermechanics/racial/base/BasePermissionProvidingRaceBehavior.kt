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
package com.fablesfantasyrp.plugin.charactermechanics.racial.base

import com.fablesfantasyrp.plugin.charactermechanics.frunBlocking
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.event.CharacterChangeTraitsEvent
import com.fablesfantasyrp.plugin.hacks.PermissionInjector
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.utils.TransactionStep
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

abstract class BasePermissionProvidingRaceBehavior(race: Race,
												   plugin: Plugin,
												   characters: CharacterRepository,
												   profileManager: ProfileManager,
												   private val permissionInjector: PermissionInjector)
	: BaseRaceBehavior(race, plugin, characters, profileManager) {

	abstract val permission: String

	override fun init() {
		super.init()
		server.pluginManager.registerEvents(BasePermissionProvidingRaceBehaviorListener(), plugin)
	}

	inner class BasePermissionProvidingRaceBehaviorListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerProfileChange(e: PlayerSwitchProfileEvent) {
			val oldCharacter = e.old?.let { frunBlocking { characters.forProfile(it) } }
			val newCharacter = e.new?.let { frunBlocking { characters.forProfile(it) } }

			val oldValue = if (oldCharacter != null && oldCharacter.race == race) true else null
			val value = if (newCharacter != null && newCharacter.race == race) true else null

			e.transaction.steps.add(TransactionStep(
				{ permissionInjector.inject(e.player, permission, value) },
				{ permissionInjector.inject(e.player, permission, oldValue) }
			))
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onCharacterChangeTraits(e: CharacterChangeTraitsEvent) {
			val player = profileManager.getCurrentForProfile(e.character.profile) ?: return
			val value = if (e.character.race == race) true else null
			permissionInjector.inject(player, permission, value)
		}
	}
}
