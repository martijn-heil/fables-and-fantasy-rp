package com.fablesfantasyrp.plugin.lodestones.dal.model

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.Named
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

data class LodestoneData(
	val location: BlockIdentifier,
	override val name: String,
	val isPublic: Boolean = false,
	override val id: Int = 0) : Identifiable<Int>, Named
