/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.text.sendError
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*

internal class ProfileManagerImpl(private val server: Server) : ProfileManager {
	private val currentProfiles = HashMap<UUID, Profile>()
	private val currentProfilesTwo = HashMap<Profile, UUID>()

	override fun getCurrentForPlayer(player: Player) = currentProfiles[player.uniqueId]

	@Throws(ProfileSwitchException::class)
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

		val event = PlayerSwitchProfileEvent(player, currentProfile, profile)
		server.pluginManager.callEvent(event)
		try {
			event.transaction.execute()
		} catch (ex: Exception) {
			player.sendError("An unknown error occurred during profile switch.")
			throw ProfileSwitchException(ex)
		}

		currentProfilesTwo.remove(currentProfile)
		currentProfiles[player.uniqueId] = profile
		currentProfilesTwo[profile] = player.uniqueId

		server.pluginManager.callEvent(PostPlayerSwitchProfileEvent(player, currentProfile, profile))
	}

	override fun stopTracking(player: Player) {
		val currentProfile = this.getCurrentForPlayer(player) ?: return

		val event = PlayerSwitchProfileEvent(player, currentProfile, null)
		server.pluginManager.callEvent(event)
		try {
			event.transaction.execute()
		} catch (ex: Exception) {
			player.sendError("An unknown error occurred during profile switch.")
			throw ProfileSwitchException(ex)
		}

		val result = currentProfiles.remove(player.uniqueId)
		currentProfilesTwo.remove(result)

		server.pluginManager.callEvent(PostPlayerSwitchProfileEvent(player, currentProfile, null))
	}

	override fun getCurrentForProfile(profile: Profile): Player? {
		return currentProfilesTwo[profile]?.let { server.getPlayer(it) }
	}

	override fun getActive(): Map<Player, Profile>
		= currentProfiles.mapKeys { server.getPlayer(it.key) }.filterKeys { it != null } as Map<Player, Profile>
}
