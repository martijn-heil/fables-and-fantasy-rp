package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.playerinstance.event.PrePlayerSwitchPlayerInstanceEvent
import com.fablesfantasyrp.plugin.utils.isRealPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import java.time.Instant

class CharactersListener(private val characters: EntityCharacterRepository) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchPlayerInstance(e: PrePlayerSwitchPlayerInstanceEvent) {
		val old = e.old ?: return
		val character = characters.forPlayerInstance(old) ?: return
		character.lastSeen = Instant.now()
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerInteractAtPlayer(e: PlayerInteractAtEntityEvent) {
		val target = e.rightClicked as? Player ?: return
		if (!target.isRealPlayer) return

		val character = target.currentPlayerCharacter ?: return
		e.player.sendMessage(characterCard(character))
	}
}
