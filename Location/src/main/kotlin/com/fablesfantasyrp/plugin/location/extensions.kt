package com.fablesfantasyrp.plugin.location

import com.fablesfantasyrp.plugin.location.data.entity.ProfileLocationRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.koin.core.context.GlobalContext

var Profile.location
	get() = GlobalContext.get().get<ProfileLocationRepository>().forOwner(this).location
	set(value) { GlobalContext.get().get<ProfileLocationRepository>().forOwner(this).location = value }
