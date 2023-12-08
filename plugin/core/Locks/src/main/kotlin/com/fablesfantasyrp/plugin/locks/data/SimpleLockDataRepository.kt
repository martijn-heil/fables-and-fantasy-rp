package com.fablesfantasyrp.plugin.locks.data

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import org.bukkit.Location

interface SimpleLockDataRepository : MutableRepository<SimpleLockData>, KeyedRepository<Location, SimpleLockData> {
	fun forLocation(location: Location) = this.forId(location)
}
