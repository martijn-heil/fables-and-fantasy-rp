package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.profile.event.PrePlayerSwitchProfileEvent
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*

internal class ProfileManagerImpl(private val server: Server) : ProfileManager {
	private val currentProfiles = HashMap<UUID, Profile>()
	private val currentProfilesTwo = HashMap<Profile, UUID>()

	override fun getCurrentForPlayer(player: Player) = currentProfiles[player.uniqueId]

	@Throws(ProfileOccupiedException::class)
	override fun setCurrentForPlayer(player: Player, profile: Profile, force: Boolean) {
		val currentProfile = this.getCurrentForPlayer(player)
		if (currentProfile == profile) return

		val currentHolder = currentProfilesTwo[profile]
		if (currentHolder != null) {
			if (force) {
				this.stopTracking(server.getPlayer(currentHolder)!!)
			} else {
				throw ProfileOccupiedException(server.getPlayer(currentHolder)!!)
			}
		}

		server.pluginManager.callEvent(PrePlayerSwitchProfileEvent(player, currentProfile, profile))

		currentProfilesTwo.remove(currentProfile)
		currentProfiles[player.uniqueId] = profile
		currentProfilesTwo[profile] = player.uniqueId

		server.pluginManager.callEvent(PostPlayerSwitchProfileEvent(player, currentProfile, profile))
	}

	override fun stopTracking(player: Player) {
		val currentProfile = this.getCurrentForPlayer(player) ?: return

		server.pluginManager.callEvent(PrePlayerSwitchProfileEvent(player, currentProfile, null))

		val result = currentProfiles.remove(player.uniqueId)
		currentProfilesTwo.remove(result)

		server.pluginManager.callEvent(PostPlayerSwitchProfileEvent(player, currentProfile, null))
	}

	override fun getCurrentForProfile(profile: Profile): Player? {
		return currentProfilesTwo[profile]?.let { server.getPlayer(it) }
	}
}
