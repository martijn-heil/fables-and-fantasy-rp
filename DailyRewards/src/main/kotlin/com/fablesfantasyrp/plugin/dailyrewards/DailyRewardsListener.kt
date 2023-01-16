package com.fablesfantasyrp.plugin.dailyrewards

import com.denizenscript.denizen.objects.PlayerTag
import com.fablesfantasyrp.plugin.denizeninterop.denizenRun
import com.fablesfantasyrp.plugin.playerinstance.event.PostPlayerSwitchPlayerInstanceEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class DailyRewardsListener : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchPlayerInstance(e: PostPlayerSwitchPlayerInstanceEvent) {
		denizenRun("dailyrewards_check", mapOf("player" to PlayerTag(e.player)))
	}
}
