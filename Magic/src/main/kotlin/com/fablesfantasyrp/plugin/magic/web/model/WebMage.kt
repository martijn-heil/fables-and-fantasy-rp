package com.fablesfantasyrp.plugin.magic.web.model

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import kotlinx.serialization.Serializable

@Serializable
data class WebMage(
	val id: Long,
	val magicPath: MagicPath,
	val magicLevel: Int,
	val spells: List<String>)
fun Mage.transform() = WebMage(
	id = id,
	magicPath = magicPath,
	magicLevel = magicLevel,
	spells = spells.map { it.id }
)
