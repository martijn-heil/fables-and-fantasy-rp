package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofnight

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object BloodSucker : MageAbility {
	override val id = "blood_sucker"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_NIGHT
	override val displayName: String = "Blood Sucker"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}