package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.playerinstance.PlayerSwitchPlayerInstanceEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.time.Instant

class CharactersListener(private val characters: EntityCharacterRepository) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchPlayerInstance(e: PlayerSwitchPlayerInstanceEvent) {
		val old = e.old ?: return
		val character = characters.forPlayerInstance(old) ?: return
		character.lastSeen = Instant.now()
	}
}
