package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingNamedEntityRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.mapper.LodestoneMapper
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class LodestoneRepositoryImpl(child: LodestoneMapper) : MassivelyCachingNamedEntityRepository<Int, Lodestone, LodestoneMapper>(child), LodestoneRepository {
    private val byLocation = HashMap<BlockIdentifier, Lodestone>()

	override fun init() {
		super.init()
		strongCache.forEach { byLocation[it.location] = it }
	}

	override fun forLocation(location: BlockIdentifier): Lodestone? {
		return byLocation[location]
	}

	override fun create(v: Lodestone): Lodestone {
		val created = super.create(v)
		byLocation[created.location] = created
		return created
	}

	override fun forId(id: Int): Lodestone? {
		val found = super.forId(id)
		if (found != null) byLocation[found.location] = found
		return found
	}

	override fun destroy(v: Lodestone) {
		super.destroy(v)
		byLocation.remove(v.location)
		v.isDestroyed = true
	}

	override fun markDirty(v: Lodestone, what: String, oldValue: Any?, newValue: Any?) {
		super.markDirty(v, what, oldValue, newValue)

		if (what == "location") {
			if (oldValue != null) byLocation.remove(oldValue)
			if (newValue != null) byLocation[newValue as BlockIdentifier] = v
		}
	}
}
