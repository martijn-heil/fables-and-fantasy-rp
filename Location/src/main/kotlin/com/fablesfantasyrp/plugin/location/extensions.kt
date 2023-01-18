package com.fablesfantasyrp.plugin.location

import com.fablesfantasyrp.plugin.profile.data.entity.Profile

var Profile.location
	get() = PLUGIN.profileLocationRepository.forOwner(this).location
	set(value) { PLUGIN.profileLocationRepository.forOwner(this).location = value }
