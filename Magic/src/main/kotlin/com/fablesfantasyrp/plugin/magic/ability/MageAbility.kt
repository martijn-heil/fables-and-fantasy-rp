package com.fablesfantasyrp.plugin.magic.ability

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

interface MageAbility {
	val id: String
	val displayName: String
	val description: String
	val minimumMageLevel: Int
	val magicPath: MagicPath

	fun applyTo(mage: Mage)
}
