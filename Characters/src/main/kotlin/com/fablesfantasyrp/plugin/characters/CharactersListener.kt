package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.event.PostPlayerSwitchPlayerInstanceEvent
import com.fablesfantasyrp.plugin.utils.isRealPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import java.time.Instant

class CharactersListener(private val characters: EntityCharacterRepository,
						 private val playerInstanceManager: PlayerInstanceManager) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchPlayerInstance(e: PostPlayerSwitchPlayerInstanceEvent) {
		val old = e.old ?: return
		val character = characters.forPlayerInstance(old) ?: return
		character.lastSeen = Instant.now()
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchPlayerInstance2(e: PostPlayerSwitchPlayerInstanceEvent) {
		val newCharacter = e.new?.let { characters.forPlayerInstance(it) }
		e.player.dFlags.setFlag("characters_name", newCharacter?.let { ElementTag(it.name) }, null)
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerInteractAtPlayer(e: PlayerInteractAtEntityEvent) {
		val target = e.rightClicked as? Player ?: return
		if (!target.isRealPlayer) return

		val currentPlayerInstance = playerInstanceManager.getCurrentForPlayer(target) ?: return
		val character = characters.forPlayerInstance(currentPlayerInstance) ?: return
		e.player.sendMessage(characterCard(character))
	}
}
