package com.fablesfantasyrp.plugin.magic.ability.aeromancy

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object Cloud : MageAbility {
	override val id = "cloud"
	override val magicPath = MagicPath.AEROMANCY
	override val minimumMageLevel: Int = 3
	override val displayName: String = "Cloud"
	override val description: String =
			"The caster can create a cloud to sit on, giving the user a +1 to any spell roll," +
			" but making their movement 0. Sitting on this cloud will heal small cuts and bruises slowly," +
			" which is not applicable in combat situations."

	override fun applyTo(mage: Mage) {

	}
}
