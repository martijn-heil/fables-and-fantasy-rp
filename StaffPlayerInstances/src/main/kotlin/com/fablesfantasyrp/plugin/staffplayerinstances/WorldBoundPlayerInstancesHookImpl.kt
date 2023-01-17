package com.fablesfantasyrp.plugin.staffplayerinstances

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.utils.FLATROOM
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.WorldRestrictionRule
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.WorldRestrictionRuleAction
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.WorldRestrictionRuleRepository

class WorldBoundPlayerInstancesHookImpl : WorldBoundPlayerInstancesHook {
	private val worldRestrictionRules = Services.get<WorldRestrictionRuleRepository>()

	override fun allowToFlatroom(playerInstance: PlayerInstance) {
		worldRestrictionRules.updateOrCreate(
				WorldRestrictionRule(Pair(playerInstance, FLATROOM!!.uid), WorldRestrictionRuleAction.ALLOWED))
	}
}
