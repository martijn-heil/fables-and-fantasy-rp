package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofnight

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object VampiricHover : MageAbility {
	override val id = "vampiric_hover"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_NIGHT
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Vampiric Hover"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
