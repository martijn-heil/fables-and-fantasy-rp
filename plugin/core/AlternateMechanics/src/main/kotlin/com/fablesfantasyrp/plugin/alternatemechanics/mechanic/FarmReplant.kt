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
package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import com.fablesfantasyrp.plugin.alternatemechanics.flaunch
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.github.shynixn.mccoroutine.bukkit.launch
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin

class FarmReplant(private val plugin: Plugin,
				  private val profileManager: ProfileManager,
				  private val characters: CharacterRepository,
				  private val worldGuard: WorldGuardPlugin,
				  private val regionContainer: RegionContainer) : Mechanic {
	private val server = plugin.server
	private val crops = hashSetOf(Material.WHEAT, Material.CARROTS, Material.BEETROOTS, Material.POTATOES)

	override fun init() {
		server.pluginManager.registerEvents(FarmReplantListener(), plugin)
	}

	inner class FarmReplantListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerFarm(e: PlayerInteractEvent) {
			flaunch {
				if (e.hand != EquipmentSlot.HAND) return@flaunch
				if (e.player.inventory.itemInMainHand.type != Material.WOODEN_HOE) return@flaunch
				val block = e.clickedBlock ?: return@flaunch
				if (!crops.contains(block.type)) return@flaunch

				val canBuild = regionContainer.createQuery().testBuild(BukkitAdapter.adapt(block.location), worldGuard.wrapPlayer(e.player))
				if (!canBuild) return@flaunch

				val blockData = (block.blockData as Ageable)
				if (blockData.age == 0) return@flaunch

				val location = block.location
				val tool = e.player.inventory.itemInMainHand
				val drops = block.getDrops(tool, e.player)

				val character = profileManager.getCurrentForPlayer(e.player)?.let { characters.forProfile(it) }
				if (character != null && character.traits.contains(CharacterTrait.HINTISH_HERITAGE)) {
					drops.forEach { it.amount *= 2 }
				}

				blockData.age = 0
				block.blockData = blockData

				location.world.playSound(location, Sound.BLOCK_CROP_BREAK, 1.0f, 1.0f)
				drops.forEach { location.world.dropItem(location, it) }
			}
		}
	}
}
