package com.fablesfantasyrp.plugin.magic.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.Location

interface TearRepository : MutableRepository<Tear>, KeyedRepository<Long, Tear> {
	fun forOwner(owner: Mage): Collection<Tear>
	fun forLocation(location: Location): Tear?
}
