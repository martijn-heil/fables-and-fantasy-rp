package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomy
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

var Profile.money: Int
	get() = PLUGIN.profileEconomyRepository.forProfile(this).money
	set(value) { PLUGIN.profileEconomyRepository.forProfile(this).money = value }

val Profile.economy: ProfileEconomy
	get() = PLUGIN.profileEconomyRepository.forProfile(this)
