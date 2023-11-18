package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.MapBox

interface MapBoxRepository : Repository<MapBox>, KeyedRepository<Int, MapBox> {
}
