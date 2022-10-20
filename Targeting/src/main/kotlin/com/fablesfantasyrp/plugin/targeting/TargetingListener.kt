package com.fablesfantasyrp.plugin.targeting

import com.fablesfantasyrp.plugin.text.sendError
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.*

class TargetingListener : Listener {
	private val cooldowns = HashMap<UUID, Long>()

	private fun rightClickCoolDown(p: Player): Boolean {
		val lastTimeClicked = cooldowns[p.uniqueId]
		if (lastTimeClicked != null && System.currentTimeMillis() - lastTimeClicked < 500) return true
		cooldowns[p.uniqueId] = System.currentTimeMillis()
		return false
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	fun onPlayerRightClick(e: PlayerInteractEvent) {
		if (e.hand != EquipmentSlot.HAND) return
		if (rightClickCoolDown(e.player)) return

		val data = targetingPlayerDataRepository.forOfflinePlayer(e.player)
		if (!data.isSelecting) return
		e.isCancelled = true

		val target = e.player.getTargetEntity(100) as? Player
		if (target == null) {
			e.player.sendError("Please aim at a player and right-click.")
			return
		}

		val targets = data.targets.toMutableSet()
		if(!targets.remove(target)) {
			targets.add(target)
			e.player.sendMessage("$SYSPREFIX Added ${target.name} to your target list.")
		} else {
			e.player.sendMessage("$SYSPREFIX Removed ${target.name} from your target list.")
		}
		targetingPlayerDataRepository.update(data.copy(targets = targets))
	}
}
