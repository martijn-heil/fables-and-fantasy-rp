package com.fablesfantasyrp.plugin.worldboundprofiles.data

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import java.util.*

data class WorldRestrictionRule(override val id: Pair<Profile, UUID>, val action: WorldRestrictionRuleAction)
	: Identifiable<Pair<Profile, UUID>>
