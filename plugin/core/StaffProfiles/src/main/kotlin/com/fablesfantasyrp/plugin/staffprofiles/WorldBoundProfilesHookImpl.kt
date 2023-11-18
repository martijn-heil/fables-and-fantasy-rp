package com.fablesfantasyrp.plugin.staffprofiles

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.FLATROOM
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.worldboundprofiles.data.WorldRestrictionRule
import com.fablesfantasyrp.plugin.worldboundprofiles.data.WorldRestrictionRuleAction
import com.fablesfantasyrp.plugin.worldboundprofiles.data.WorldRestrictionRuleRepository

class WorldBoundProfilesHookImpl : WorldBoundProfilesHook {
	private val worldRestrictionRules = Services.get<WorldRestrictionRuleRepository>()

	override fun allowToFlatroom(profile: Profile) {
		worldRestrictionRules.updateOrCreate(
				WorldRestrictionRule(Pair(profile, FLATROOM!!.uid), WorldRestrictionRuleAction.ALLOWED))
	}
}
