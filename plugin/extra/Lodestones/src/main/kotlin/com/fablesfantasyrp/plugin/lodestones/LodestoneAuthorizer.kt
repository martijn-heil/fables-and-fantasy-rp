package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.entity.Player

interface LodestoneAuthorizer {
	suspend fun mayWarpTo(who: Profile?, lodestone: Lodestone): Boolean
	suspend fun useCoolDown(who: Player): Boolean
}
