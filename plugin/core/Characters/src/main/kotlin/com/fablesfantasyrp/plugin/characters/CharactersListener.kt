package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizen.objects.PlayerTag
import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.service.api.CharacterCardGenerator
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.denizeninterop.denizenRun
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.event.PlayerForceProfileSelectionEvent
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.staffprofiles.domain.repository.StaffProfileRepository
import com.fablesfantasyrp.plugin.time.event.NewDayEvent
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isRealPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin
import org.koin.core.context.GlobalContext
import java.time.Instant

class CharactersListener(private val plugin: Plugin,
						 private val characters: CharacterRepository,
						 private val profiles: EntityProfileRepository,
						 private val profileManager: ProfileManager,
						 private val staffProfiles: StaffProfileRepository) : Listener {
	private val server = plugin.server
	private val characterCardGenerator by lazy { GlobalContext.get().get<CharacterCardGenerator>() }

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchProfile(e: PostPlayerSwitchProfileEvent) {
		flaunch {
			val old = e.old ?: return@flaunch
			val character = characters.forProfile(old) ?: return@flaunch
			character.lastSeen = Instant.now()
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchProfile2(e: PostPlayerSwitchProfileEvent) {
		flaunch {
			val newCharacter = e.new?.let { characters.forProfile(it) }
			e.player.dFlags.setFlag("characters_name", newCharacter?.let { ElementTag(it.name) }, null)
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchProfile3(e: PostPlayerSwitchProfileEvent) {
		denizenRun("warpcrystal_ensure_presence", mapOf("player" to PlayerTag(e.player)))
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchProfile4(e: PostPlayerSwitchProfileEvent) {
		frunBlocking {
			val newCharacter = e.new?.let { characters.forProfile(it) } ?: return@frunBlocking
			val dateOfDeath = newCharacter.dateOfNaturalDeath ?: return@frunBlocking
			val today = FablesLocalDate.now()
			if (today.isAfter(dateOfDeath)) {
				newCharacter.isDead = true
			} else if (newCharacter.isDying) {
				sendDyingNotification(e.player, newCharacter)
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onNewDay(e: NewDayEvent) {
		flaunch {
			profileManager.getActive()
				.mapValues { characters.forProfile(it.value) }
				.filter { it.value != null }
				.forEach {
					val player = it.key
					val character = it.value!!
					character.checkNaturalDeath()
					if (character.isDying) {
						sendDyingNotification(player, character)
					}
				}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPlayerForceProfileSelection(e: PlayerForceProfileSelectionEvent) {
		if (!e.player.isWhitelisted) return

		frunBlocking {
			if (characters.activeForOwner(e.player).isNotEmpty()) return@frunBlocking
			if (staffProfiles.containsAny(profiles.activeForOwner(e.player))) return@frunBlocking

			e.isCancelled = true

			server.scheduler.scheduleSyncDelayedTask(plugin, {
				flaunch { forceCharacterCreation(e.player, profiles, characters) }
			}, 1)
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerInteractAtPlayer(e: PlayerInteractAtEntityEvent) {
		if (e.hand != EquipmentSlot.HAND) return
		val target = e.rightClicked as? Player ?: return
		if (!e.player.isSneaking) return
		if (!target.isRealPlayer) return

		flaunch {
			val currentProfile = profileManager.getCurrentForPlayer(target)
			val character = currentProfile?.let { characters.forProfile(it) } ?: run {
				e.player.sendMessage("$SYSPREFIX ${e.rightClicked.name} is currently not in-character")
				return@flaunch
			}
			e.player.sendMessage(characterCardGenerator.card(character, e.player))
		}
	}
}
