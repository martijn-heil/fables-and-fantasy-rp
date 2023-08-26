package com.fablesfantasyrp.plugin.magic.domain.repository

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.entity.Tear
import org.bukkit.Location

interface TearRepository : MutableRepository<Tear>, KeyedRepository<Long, Tear> {
	fun forOwner(owner: Mage): Collection<Tear>
	fun forLocation(location: Location): Tear?
}
