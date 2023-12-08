package com.fablesfantasyrp.plugin.basicsystem.data.entity

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.Repository

interface SlidingDoorRepository :
		Repository<SlidingDoor>,
	MutableRepository<SlidingDoor>,
	KeyedRepository<Int, SlidingDoor> {
}
