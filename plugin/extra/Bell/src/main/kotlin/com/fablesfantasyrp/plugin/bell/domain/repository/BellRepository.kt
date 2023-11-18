package com.fablesfantasyrp.plugin.bell.domain.repository

import com.fablesfantasyrp.plugin.bell.domain.entity.Bell
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.NamedRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

interface BellRepository :
		Repository<Bell>,
		MutableRepository<Bell>,
		KeyedRepository<Int, Bell>,
		NamedRepository<Bell> {
			fun forLocation(location: BlockIdentifier): Bell?
}
