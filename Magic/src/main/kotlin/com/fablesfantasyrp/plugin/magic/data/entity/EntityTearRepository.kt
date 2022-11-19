package com.fablesfantasyrp.plugin.magic.data.entity

import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.Location

class EntityTearRepository<C>(child: C) : SimpleEntityRepository<Long, Tear, C>(child), TearRepository
		where C: KeyedRepository<Long, Tear>,
			  C: MutableRepository<Tear>,
			  C: HasDirtyMarker<Tear> {

	init {
		super.all().forEach { this.markStrong(it) }
	}

	override fun destroy(v: Tear) {
		super.destroy(v)
		v.isDeleted = true
	}

	override fun create(v: Tear): Tear {
		val result = super.create(v)
		this.markStrong(result)
		return result
	}

	override fun forOwner(owner: Mage): Collection<Tear> {
		return this.all().filter { it.owner == owner }
	}

	override fun forLocation(location: Location): Tear? {
		return this.all().find { it.location.distance(location) < 0.1 }
	}
}
