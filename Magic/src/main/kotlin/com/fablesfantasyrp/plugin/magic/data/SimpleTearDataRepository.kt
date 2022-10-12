package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface SimpleTearDataRepository : MutableRepository<SimpleTearData>, KeyedRepository<Long, SimpleTearData> {
	fun forOwner(owner: MageData): Collection<SimpleTearData>
}
