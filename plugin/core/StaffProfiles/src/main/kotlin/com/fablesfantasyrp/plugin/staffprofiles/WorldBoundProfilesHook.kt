package com.fablesfantasyrp.plugin.staffprofiles

import com.fablesfantasyrp.plugin.profile.data.entity.Profile

interface WorldBoundProfilesHook {
	fun allowToFlatroom(profile: Profile)
}
