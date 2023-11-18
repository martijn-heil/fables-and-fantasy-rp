package com.fablesfantasyrp.plugin.magic.ability

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

interface MageAbility {
	val id: String
	val displayName: String
	val description: String
	val minimumMageLevel: Int
	val magicPath: MagicPath

	fun applyTo(mage: Mage)
}
