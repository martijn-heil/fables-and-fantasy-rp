package com.fablesfantasyrp.plugin.lodestones.command.provider

import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository
import com.fablesfantasyrp.caturix.parametric.AbstractModule

class LodestoneModule(private val lodestones: LodestoneRepository,
					  private val banners: LodestoneBannerRepository) : AbstractModule() {
	override fun configure() {
		bind(Lodestone::class.java).toProvider(LodestoneProvider(lodestones))
		bind(LodestoneBanner::class.java).toProvider(LodestoneBannerProvider(banners))
	}
}
