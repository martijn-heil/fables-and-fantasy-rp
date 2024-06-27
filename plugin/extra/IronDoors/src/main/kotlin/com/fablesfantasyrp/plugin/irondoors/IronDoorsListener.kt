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
package com.fablesfantasyrp.plugin.irondoors

import com.fablesfantasyrp.plugin.locks.FablesLocks.Companion.lockRepository
import com.fablesfantasyrp.plugin.locks.data.LockAccess
import com.fablesfantasyrp.plugin.locks.data.LockData
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockState
import org.bukkit.block.data.Openable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class IronDoorsListener : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerIronDoorInteract(e: PlayerInteractEvent) {
		val p = e.player
		if(p.isSneaking) return
		if (e.action != Action.RIGHT_CLICK_BLOCK) return
		val clickedBlock = e.clickedBlock ?: return
		val correct_block = clickedBlock.type == Material.IRON_TRAPDOOR || clickedBlock.type == Material.IRON_DOOR
		if (!correct_block) return
		if (e.hand == EquipmentSlot.OFF_HAND) return
		if (p.inventory.itemInMainHand.type == Material.WOODEN_HOE ) return

		val lock: LockData? = lockRepository.forLocation(clickedBlock.location)
		if (lock != null && lock.calculateAccess(p) == LockAccess.NONE) return

		val state: BlockState = clickedBlock.state
		val doorOpen: Openable = state.blockData as Openable
		doorOpen.isOpen = !doorOpen.isOpen

		val world = p.world
		val sound = if (doorOpen.isOpen) Sound.BLOCK_IRON_DOOR_OPEN else Sound.BLOCK_IRON_DOOR_CLOSE
		world.playSound(clickedBlock.location, sound, 1f, 1f)

		state.blockData = doorOpen
		state.update()
	}
}
