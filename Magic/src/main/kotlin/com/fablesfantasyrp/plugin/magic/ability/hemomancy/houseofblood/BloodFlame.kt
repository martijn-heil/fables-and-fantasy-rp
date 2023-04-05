package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofblood

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object BloodFlame : MageAbility {
	override val id = "blood_flame"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_BLOOD
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Blood Flame"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
