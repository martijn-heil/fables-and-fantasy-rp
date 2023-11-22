package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.MapBox
import org.bukkit.Location
import org.bukkit.World

interface MapBoxRepository : Repository<MapBox>, KeyedRepository<Int, MapBox> {
	fun forWorld(world: World): MapBox?
	fun anyContains(location: Location): Boolean
	fun forLocation(location: Location): MapBox?
}
