package com.fablesfantasyrp.plugin.magic.domain.repository

import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Tear
import org.bukkit.Location

class TearRepositoryImpl : SimpleMapRepository<Long, Tear>(), TearRepository, HasDirtyMarker<Tear> {
	override var dirtyMarker: DirtyMarker<Tear>? = null

	private var idCounter = 0L
	override fun create(v: Tear): Tear {
		v.dirtyMarker = dirtyMarker
		return super.create(Tear(
			id = idCounter++,
			location = v.location,
			magicType = v.magicType,
			owner = v.owner,
		))
	}

	override fun forOwner(owner: Character): Collection<Tear> {
		return this.all().filter { it.owner.id == owner.id }
	}

	override fun forLocation(location: Location): Tear? {
		return this.all().find { it.location == location }
	}
}
