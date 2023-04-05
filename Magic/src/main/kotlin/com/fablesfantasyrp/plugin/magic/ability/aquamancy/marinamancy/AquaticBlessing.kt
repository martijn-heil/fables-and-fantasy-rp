package com.fablesfantasyrp.plugin.magic.ability.aquamancy.marinamancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object AquaticBlessing : MageAbility {
	override val id = "aquatic_blessing"
	override val magicPath = MagicPath.MARINAMANCY
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Aquatic Blessing"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
