package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.profile.event.PrePlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.SPAWN
import com.fablesfantasyrp.plugin.utils.isVanished
import com.github.shynixn.mccoroutine.bukkit.launch
import de.myzelyam.api.vanish.VanishAPI
import kotlinx.coroutines.CancellationException
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.LOW
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class ProfileListener(private val plugin: JavaPlugin,
					  private val profiles: EntityProfileRepository,
					  private val profileManager: ProfileManager) : Listener {
	private val server get() = plugin.server
	private val playersCurrentlySwitchingProfile: MutableSet<Player> = HashSet()

	private suspend fun forceProfileSelection(player: Player, ownedProfiles: Collection<Profile>) {
		profileManager.stopTracking(player) // Just to be safe
		player.teleport(SPAWN)
		player.inventory.clear()
		player.enderChest.clear()
		require(profileManager.getCurrentForPlayer(player) == null)

		do {
			val selector = server.servicesManager.getRegistration(ProfilePrompter::class.java)!!.provider
			try {
				if (!player.isOnline || server.isStopping) return
				val wasVanished = player.isVanished
				if (!wasVanished) VanishAPI.hidePlayer(player)
				val newProfile = selector.promptSelect(player, ownedProfiles)
				profileManager.setCurrentForPlayer(player, newProfile)
				player.sendMessage("$SYSPREFIX You are now profile #${newProfile.id}")
				if (!wasVanished) VanishAPI.showPlayer(player)
			} catch (ex: ProfileOccupiedException) {
				player.sendError("This profile is currently occupied.")
			} catch (_: CancellationException) {}
		} while (profileManager.getCurrentForPlayer(player) == null)
		require(profileManager.getCurrentForPlayer(player) != null)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		if (!e.player.isWhitelisted) return // TODO leaky abstraction from whitelist plugin

		val ownedProfiles = profiles.forOwner(e.player).filter { it.isActive }
		if (ownedProfiles.isEmpty()) return

		server.scheduler.scheduleSyncDelayedTask(plugin, {
			val player = server.getPlayer(e.player.uniqueId) ?: return@scheduleSyncDelayedTask
			plugin.launch { forceProfileSelection(player, ownedProfiles) }
		}, 1)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onProfileSwitch(e: PostPlayerSwitchProfileEvent) {
		if (e.new == null && e.old != null) {
			val ownedProfiles = profiles.forOwner(e.player).filter { it.isActive }
			plugin.launch { forceProfileSelection(e.player, ownedProfiles) }
		}
		playersCurrentlySwitchingProfile.remove(e.player)
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPreProfileSwitch(e: PrePlayerSwitchProfileEvent) {
		playersCurrentlySwitchingProfile.add(e.player)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		profileManager.stopTracking(e.player)
		playersCurrentlySwitchingProfile.remove(e.player)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerMove(e: PlayerMoveEvent) {
		if (!playersCurrentlySwitchingProfile.contains(e.player) &&
				profileManager.getCurrentForPlayer(e.player) == null) {
			e.isCancelled = true
		}
	}
}
