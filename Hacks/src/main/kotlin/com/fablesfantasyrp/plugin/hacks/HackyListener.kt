package com.fablesfantasyrp.plugin.hacks

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import org.bukkit.event.Listener

class HackyListener(private val characters: CharacterRepository,
					private val flippedPlayerManager: FlippedPlayerManager) : Listener {
	/*@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	fun onPlayerProfileChange(e: PrePlayerSwitchProfileEvent) {
		if (e.player.uniqueId != NINJOH_UUID) return
		val flipped = e.new?.let { characters.forProfile(it) } == null
		flippedPlayerManager.setFlipped(e.player, flipped)
	}*/
}
