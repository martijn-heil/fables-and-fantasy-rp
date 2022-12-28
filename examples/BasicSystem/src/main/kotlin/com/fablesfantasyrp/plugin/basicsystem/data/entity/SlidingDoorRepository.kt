package com.fablesfantasyrp.plugin.basicsystem.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.Repository

interface SlidingDoorRepository :
		Repository<SlidingDoor>,
		MutableRepository<SlidingDoor>,
		KeyedRepository<Int, SlidingDoor> {
}
