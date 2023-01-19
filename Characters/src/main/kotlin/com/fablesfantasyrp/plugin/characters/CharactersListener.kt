package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.event.PlayerForceProfileSelectionEvent
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.staffprofiles.data.StaffProfileRepository
import com.fablesfantasyrp.plugin.utils.isRealPlayer
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.plugin.Plugin
import java.time.Instant

class CharactersListener(private val plugin: Plugin,
						 private val characters: EntityCharacterRepository,
						 private val profiles: EntityProfileRepository,
						 private val profileManager: ProfileManager,
						 private val staffProfiles: StaffProfileRepository) : Listener {
	private val server = plugin.server

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

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPlayerForceProfileSelection(e: PlayerForceProfileSelectionEvent) {
		if (!e.player.isWhitelisted) return

		if (characters.activeForOwner(e.player).isNotEmpty()) return
		if (staffProfiles.containsAny(profiles.activeForOwner(e.player))) return

		e.isCancelled = true

		server.scheduler.scheduleSyncDelayedTask(plugin, {
			plugin.launch { forceCharacterCreation(e.player, profiles, characters) }
		}, 1)
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
