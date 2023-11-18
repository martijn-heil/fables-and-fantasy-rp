package com.fablesfantasyrp.plugin.bell.domain.repository

import com.fablesfantasyrp.plugin.bell.domain.entity.Bell
import com.fablesfantasyrp.plugin.bell.domain.mapper.BellMapper
import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingNamedEntityRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class BellRepositoryImpl(child: BellMapper) : MassivelyCachingNamedEntityRepository<Int, Bell, BellMapper>(child), BellRepository {
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
