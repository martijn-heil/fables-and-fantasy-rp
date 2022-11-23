package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.playerinstance.gui.PlayerInstanceSelectionGui
import com.github.shynixn.mccoroutine.bukkit.launch
import de.myzelyam.api.vanish.VanishAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerInstanceListener : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		PLUGIN.launch {
			if (!e.player.isOnline) return@launch
			VanishAPI.hidePlayer(e.player)
			e.player.currentPlayerInstance = e.player.promptGui(PlayerInstanceSelectionGui(PLUGIN,
					playerInstances.forOwner(e.player).asSequence()))
			VanishAPI.showPlayer(e.player)
		}
	}
}
