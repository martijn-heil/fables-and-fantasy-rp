package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

interface LodestoneAuthorizer {
	fun mayWarpTo(who: Profile?, lodestone: Lodestone): Boolean
}
