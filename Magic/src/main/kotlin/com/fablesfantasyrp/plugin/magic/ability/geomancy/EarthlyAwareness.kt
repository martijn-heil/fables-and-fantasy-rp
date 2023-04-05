package com.fablesfantasyrp.plugin.magic.ability.geomancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object EarthlyAwareness : MageAbility {
	override val id = "earthly_awareness"
	override val magicPath = MagicPath.GEOMANCY
	override val minimumMageLevel: Int = 3
	override val displayName: String = "Earthly Awareness"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
