package com.fablesfantasyrp.plugin.magic.ability.geomancy.petromancy

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object CrystallineHands : MageAbility {
	override val id = "crystalline_hands"
	override val magicPath = MagicPath.PETROMANCY
	override val minimumMageLevel: Int = 8
	override val displayName: String = "crystalline_hands"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
