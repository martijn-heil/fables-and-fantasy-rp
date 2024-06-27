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

import com.fablesfantasyrp.plugin.charactermechanics.frunBlocking
import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.knockout.knockout
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isRealPlayer
import kotlinx.coroutines.runBlocking
import org.bukkit.EntityEffect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.plugin.Plugin

class TooAngryToDie(plugin: Plugin,
					characters: CharacterRepository,
					profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.TOO_ANGRY_TO_DIE, plugin, characters, profileManager) {

	override fun init() {
		super.init()

		server.pluginManager.registerEvents(TooAngryToDieListener(), plugin)
	}

	private fun activate(player: Player) {
		player.playEffect(EntityEffect.TOTEM_RESURRECT)
	}

	inner class TooAngryToDieListener : Listener {
		@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
		fun onPlayerDamageByEntity(e: EntityDamageByEntityEvent) {
			val player = e.entity as? Player ?: return
			if (!player.isRealPlayer) return
			if (player.knockout.isKnockedOut) return

			runBlocking {

				val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return@runBlocking

				if (!character.traits.contains(trait)) return@runBlocking

				if (player.health - e.finalDamage <= 0) {
					e.isCancelled = true
					activate(player)
				}
			}
		}

		@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
		fun onPlayerDamageByBlock(e: EntityDamageByBlockEvent) {
			val player = e.entity as? Player ?: return
			if (!player.isRealPlayer) return
			if (player.knockout.isKnockedOut) return

			frunBlocking {
				val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return@frunBlocking

				if (!character.traits.contains(trait)) return@frunBlocking

				if (player.health - e.finalDamage <= 0) {
					e.isCancelled = true
					activate(player)
				}
			}
		}

		@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
		fun onPlayerDamage(e: EntityDamageEvent) {
			if (e is EntityDamageByBlockEvent || e is EntityDamageByEntityEvent) return
			val player = e.entity as? Player ?: return
			if (!player.isRealPlayer) return
			if (player.knockout.isKnockedOut) return

			frunBlocking {
				val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return@frunBlocking

				if (!character.traits.contains(trait)) return@frunBlocking

				if (player.health - e.finalDamage <= 0) {
					e.isCancelled = true
					activate(player)
				}
			}
		}
	}
}
