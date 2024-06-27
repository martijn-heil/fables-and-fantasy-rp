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

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.utils.TransactionStep
import com.fablesfantasyrp.plugin.utils.every
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

class NaturallyStealthy(plugin: Plugin,
						characters: CharacterRepository,
						profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.NATURALLY_STEALTHY, plugin, characters, profileManager) {
	private val effect = PotionEffect(PotionEffectType.INVISIBILITY, 600, 1, false, true, false)
	private val lastMoved = HashMap<Player, Instant>()
	private val invisible = HashSet<Player>()

	override fun init() {
		super.init()

		server.pluginManager.registerEvents(NaturallyStealthyListener(), plugin)

		every(plugin, 50.milliseconds) {
			getPlayersWithTrait()
				.onEach {
					val lastMoved = lastMoved[it.player]
					if (lastMoved != null && Duration.between(lastMoved, Instant.now()).seconds > 3) {
						if (it.player.isSneaking) setInvisible(it.player, true)
					} else {
						setInvisible(it.player, false)
					}
				}.collect()

				invisible.forEach { it.addPotionEffect(effect) }
		}
	}

	fun isInvisible(player: Player) = invisible.contains(player)

	private fun setInvisible(player: Player, value: Boolean) {
		if (value) {
			invisible.add(player)
			player.addPotionEffect(effect)
		} else {
			invisible.remove(player)
			player.removePotionEffect(PotionEffectType.INVISIBILITY)
		}
	}

	inner class NaturallyStealthyListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerProfileChange(e: PlayerSwitchProfileEvent) {
			val oldValue = isInvisible(e.player)

			e.transaction.steps.add(TransactionStep(
				{ setInvisible(e.player, false) },
				{ setInvisible(e.player, oldValue) }
			))
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerMove(e: PlayerMoveEvent) {
			if (e.hasExplicitlyChangedPosition()) {
				lastMoved[e.player] = Instant.now()
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			lastMoved.remove(e.player)
			setInvisible(e.player, false)
		}
	}
}
