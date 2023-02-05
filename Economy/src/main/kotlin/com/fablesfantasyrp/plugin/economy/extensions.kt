package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomy
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.koin.core.context.GlobalContext

var Profile.money: Int
	get() = GlobalContext.get().get<ProfileEconomyRepository>().forProfile(this).money
	set(value) { GlobalContext.get().get<ProfileEconomyRepository>().forProfile(this).money = value }

val Profile.economy: ProfileEconomy
	get() = GlobalContext.get().get<ProfileEconomyRepository>().forProfile(this)
