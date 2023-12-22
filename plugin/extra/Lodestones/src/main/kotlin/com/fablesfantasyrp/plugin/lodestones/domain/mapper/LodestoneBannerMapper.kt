package com.fablesfantasyrp.plugin.lodestones.domain.mapper

import com.fablesfantasyrp.plugin.database.sync.repository.base.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.lodestones.dal.model.LodestoneBannerData
import com.fablesfantasyrp.plugin.lodestones.dal.repository.LodestoneBannerDataRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.Location

class LodestoneBannerMapper(private val child: LodestoneBannerDataRepository, private val lodestones: LodestoneRepository)
	: MappingRepository<Int, LodestoneBannerData, LodestoneBanner, LodestoneBannerDataRepository>(child), LodestoneBannerRepository, HasDirtyMarker<LodestoneBanner> {

	override var dirtyMarker: DirtyMarker<LodestoneBanner>? = null

	override fun convertFromChild(v: LodestoneBannerData) = LodestoneBanner(
		id = v.id,
		location = v.location,
		lodestone = lodestones.forId(v.lodestoneId)!!,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: LodestoneBanner) = LodestoneBannerData(
		id = v.id,
		location = v.location,
		lodestoneId = v.lodestone.id
	)

	override fun forLocation(location: BlockIdentifier): LodestoneBanner? { throw NotImplementedError() }
	override fun near(location: Location): LodestoneBanner? { throw NotImplementedError() }
}
