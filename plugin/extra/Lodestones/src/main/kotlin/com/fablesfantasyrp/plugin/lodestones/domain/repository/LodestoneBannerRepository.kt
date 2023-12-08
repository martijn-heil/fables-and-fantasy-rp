package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.Location

interface LodestoneBannerRepository :
		HasDestroyHandler<LodestoneBanner>,
	MutableRepository<LodestoneBanner>,
	KeyedRepository<Int, LodestoneBanner> {
			fun forLocation(location: BlockIdentifier): LodestoneBanner?
			fun near(location: Location): LodestoneBanner?
}
