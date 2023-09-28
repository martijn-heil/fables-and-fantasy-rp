package com.fablesfantasyrp.plugin.magic.ability.necromancy.deathdestined

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object NecromanticResistance : MageAbility {
	override val id = "necromantic_resistance"
	override val magicPath = MagicPath.NECROMANCY_DEATH_DESTINED
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Necromantic Resistance"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
