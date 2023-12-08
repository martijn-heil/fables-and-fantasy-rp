package com.fablesfantasyrp.plugin.lodestones.dal.model

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

data class LodestoneBannerData(
	val location: BlockIdentifier,
	val lodestoneId: Int,
	override val id: Int = 0) : Identifiable<Int>
