package com.fablesfantasyrp.plugin.targeting

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

class TargetingListener : Listener {
	private val cooldowns = HashMap<UUID, Long>()

	private fun rightClickCoolDown(p: Player): Boolean {
		val lastTimeClicked = cooldowns[p.uniqueId]
		if (lastTimeClicked != null && System.currentTimeMillis() - lastTimeClicked < 50) return true
		cooldowns[p.uniqueId] = System.currentTimeMillis()
		return false
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerRightClick(e: PlayerInteractEvent) {
		if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return
		if (rightClickCoolDown(e.player)) return

		val target = e.player.getTargetEntity(128) as? Player ?: return
		if (!e.player.canSee(target)) return

		val data = targetingPlayerDataRepository.forOfflinePlayer(e.player)
		val targets = data.targets.toMutableList()
		if(!targets.remove(target)) { targets.add(target) }
	}
}
