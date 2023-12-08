package com.fablesfantasyrp.plugin.economy.data.entity

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.Repository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

interface ProfileEconomyRepository :
		Repository<ProfileEconomy>,
	MutableRepository<ProfileEconomy>,
	KeyedRepository<Int, ProfileEconomy> {
			fun forProfile(profile: Profile): ProfileEconomy
}
