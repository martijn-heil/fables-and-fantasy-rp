package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofnight

import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object CrimsonEyes : MageAbility {
	override val id = "crimson_eyes"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_NIGHT
	override val minimumMageLevel: Int = 6
	override val displayName: String = "Crimson Eyes"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
