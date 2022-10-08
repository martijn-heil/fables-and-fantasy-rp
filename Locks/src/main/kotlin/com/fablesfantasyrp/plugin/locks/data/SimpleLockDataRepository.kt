package com.fablesfantasyrp.plugin.locks.data

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.Location

interface SimpleLockDataRepository : MutableRepository<SimpleLockData>, KeyedRepository<Location, SimpleLockData> {
	fun forLocation(location: Location) = this.forId(location)
}
