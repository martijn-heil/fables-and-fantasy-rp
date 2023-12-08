package com.fablesfantasyrp.plugin.bell.domain.repository

import com.fablesfantasyrp.plugin.bell.domain.entity.Bell
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.NamedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.Repository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

interface BellRepository :
		Repository<Bell>,
	MutableRepository<Bell>,
	KeyedRepository<Int, Bell>,
	NamedRepository<Bell> {
			fun forLocation(location: BlockIdentifier): Bell?
}
