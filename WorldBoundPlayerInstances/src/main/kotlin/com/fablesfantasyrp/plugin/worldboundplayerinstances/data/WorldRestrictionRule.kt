package com.fablesfantasyrp.plugin.worldboundplayerinstances.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import java.util.*

data class WorldRestrictionRule(override val id: Pair<PlayerInstance, UUID>, val action: WorldRestrictionRuleAction)
	: Identifiable<Pair<PlayerInstance, UUID>>
