package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofnight

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object VampiricSkin : MageAbility {
	override val id = "vampiric_skin"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_NIGHT
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Vampiric Skin"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
