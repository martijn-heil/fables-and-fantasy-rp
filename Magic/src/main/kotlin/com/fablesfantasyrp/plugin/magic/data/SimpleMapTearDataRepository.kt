package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository

class SimpleMapTearDataRepository : SimpleMapRepository<Long, SimpleTearData>(), SimpleTearDataRepository {
	private var idCounter = 0L
	override fun create(v: SimpleTearData) = super.create(v.copy(id = idCounter++))

	override fun forOwner(owner: MageData): Collection<SimpleTearData> {
		return this.all().filter { it.owner.id == owner.id }
	}
}
