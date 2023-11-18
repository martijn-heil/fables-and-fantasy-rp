package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.entity.Player
import java.util.*

class ProfileOccupiedException(val by: Player) : ProfileSwitchException("The target profile was already occupied by ${by.name}")

open class ProfileSwitchException : Exception {
	constructor(message: String) : super(message)
	constructor(message: String, cause: Throwable) : super(message, cause)
	constructor(cause: Throwable) : super(cause)
}

interface ProfileManager {
	@Throws(ProfileSwitchException::class)
	fun setCurrentForPlayer(player: Player, profile: Profile, force: Boolean = false)
	fun getCurrentForPlayer(player: Player): Profile?
	fun stopTracking(player: Player)
	fun getCurrentForProfile(profile: Profile): Player?
	fun getActive(): Map<Player, Profile>
}
