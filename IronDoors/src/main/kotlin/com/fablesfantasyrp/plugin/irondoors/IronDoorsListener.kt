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

		if (e.action != Action.RIGHT_CLICK_BLOCK) return
		val clickedBlock = e.clickedBlock ?: return
		val correct_block = clickedBlock.type == Material.IRON_TRAPDOOR || clickedBlock.type == Material.IRON_DOOR
		if (!correct_block) return
		if (e.hand == EquipmentSlot.OFF_HAND) return
		if (p.inventory.itemInMainHand.type == Material.WOODEN_HOE ) return;

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
