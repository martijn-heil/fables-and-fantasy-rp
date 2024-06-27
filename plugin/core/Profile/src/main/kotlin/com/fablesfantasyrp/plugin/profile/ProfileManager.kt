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
