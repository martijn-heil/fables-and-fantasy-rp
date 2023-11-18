package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofbeast

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object ProtectiveCoat : MageAbility {
	override val id = "protective_coat"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_BEAST
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Protective Coat"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
