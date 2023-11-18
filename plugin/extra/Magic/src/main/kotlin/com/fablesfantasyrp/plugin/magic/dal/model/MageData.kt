package com.fablesfantasyrp.plugin.magic.dal.model

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath

data class MageData(
	val magicLevel: Int,
	val magicPath: MagicPath,
	val spells: List<SpellData>,
	override var id: Long = 0) : Identifiable<Long>
