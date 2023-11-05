package com.fablesfantasyrp.plugin.lodestones.command.provider

import com.fablesfantasyrp.plugin.database.command.SimpleEntityProvider
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository

class LodestoneBannerProvider(lodestoneBanners: LodestoneBannerRepository) : SimpleEntityProvider<LodestoneBanner, LodestoneBannerRepository>(lodestoneBanners) {
	override val entityName: String = "LodestoneBanner"
}
