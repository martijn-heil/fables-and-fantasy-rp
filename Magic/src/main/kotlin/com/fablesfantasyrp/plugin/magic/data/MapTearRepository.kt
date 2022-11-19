package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.magic.data.entity.Tear
import com.fablesfantasyrp.plugin.magic.data.entity.TearRepository
import org.bukkit.Location

class MapTearRepository : SimpleMapRepository<Long, Tear>(), TearRepository, HasDirtyMarker<Tear> {
	override var dirtyMarker: DirtyMarker<Tear>? = null

	private var idCounter = 0L
	override fun create(v: Tear): Tear {
		v.id = idCounter++
		v.dirtyMarker = dirtyMarker
		return super.create(v)
	}

	override fun forOwner(owner: Mage): Collection<Tear> {
		return this.all().filter { it.owner.id == owner.id }
	}

	override fun forLocation(location: Location): Tear? {
		return this.all().find { it.location == location }
	}
}
