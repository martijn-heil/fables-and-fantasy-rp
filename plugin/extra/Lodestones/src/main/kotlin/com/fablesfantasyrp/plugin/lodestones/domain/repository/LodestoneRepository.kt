package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.NamedRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

interface LodestoneRepository :
		HasDestroyHandler<Lodestone>,
	MutableRepository<Lodestone>,
	KeyedRepository<Int, Lodestone>,
	NamedRepository<Lodestone> {
			fun forLocation(location: BlockIdentifier): Lodestone?
}
