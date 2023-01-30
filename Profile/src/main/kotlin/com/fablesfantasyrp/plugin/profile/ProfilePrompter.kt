package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.entity.Player

interface ProfilePrompter {
	suspend fun promptSelect(player: Player, profiles: Collection<Profile>): Profile
}
