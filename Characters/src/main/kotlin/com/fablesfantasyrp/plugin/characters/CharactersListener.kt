package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.utils.isRealPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import java.time.Instant

class CharactersListener(private val characters: EntityCharacterRepository,
						 private val profileManager: ProfileManager) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchProfile(e: PostPlayerSwitchProfileEvent) {
		val old = e.old ?: return
		val character = characters.forProfile(old) ?: return
		character.lastSeen = Instant.now()
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchProfile2(e: PostPlayerSwitchProfileEvent) {
		val newCharacter = e.new?.let { characters.forProfile(it) }
		e.player.dFlags.setFlag("characters_name", newCharacter?.let { ElementTag(it.name) }, null)
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerInteractAtPlayer(e: PlayerInteractAtEntityEvent) {
		val target = e.rightClicked as? Player ?: return
		if (!target.isRealPlayer) return

		val currentProfile = profileManager.getCurrentForPlayer(target) ?: return
		val character = characters.forProfile(currentProfile) ?: return
		e.player.sendMessage(characterCard(character))
	}
}
