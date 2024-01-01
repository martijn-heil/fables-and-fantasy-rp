package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofbeast

import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object ApexInstinct : MageAbility {
	override val id = "apex_instinct"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_BEAST
	override val minimumMageLevel: Int = 10
	override val displayName: String = "Apex Instinct"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
