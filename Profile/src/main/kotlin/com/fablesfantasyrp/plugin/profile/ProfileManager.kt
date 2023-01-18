package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.entity.Player
import java.util.*

class ProfileOccupiedException(val by: Player) : Exception()

interface ProfileManager {
	@Throws(ProfileOccupiedException::class)
	fun setCurrentForPlayer(player: Player, profile: Profile, force: Boolean = false)
	fun getCurrentForPlayer(player: Player): Profile?
	fun stopTracking(player: Player)
	fun getCurrentForProfile(profile: Profile): Player?
}
