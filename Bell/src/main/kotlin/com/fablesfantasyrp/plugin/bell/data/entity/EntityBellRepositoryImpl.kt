package com.fablesfantasyrp.plugin.bell.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingNamedEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class EntityBellRepositoryImpl<C>(child: C) : MassivelyCachingNamedEntityRepository<Int, Bell, C>(child), EntityBellRepository
		where C: KeyedRepository<Int, Bell>,
			  C: MutableRepository<Bell>,
			  C: HasDirtyMarker<Bell>,
			  C: BellRepository {
    private val byLocation = HashMap<BlockIdentifier, Bell>()

	override fun init() {
		super.init()
		strongCache.forEach { byLocation[it.location] = it }
	}

	override fun forLocation(location: BlockIdentifier): Bell? {
		return byLocation[location]
	}

	override fun create(v: Bell): Bell {
		val created = super.create(v)
		byLocation[created.location] = created
		return created
	}

	override fun forId(id: Int): Bell? {
		val found = super.forId(id)
		if (found != null) byLocation[found.location] = found
		return found
	}

	override fun destroy(v: Bell) {
		super.destroy(v)
		byLocation.remove(v.location)
		v.isDestroyed = true
	}
}
