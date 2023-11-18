package com.fablesfantasyrp.plugin.magic.ability.necromancy

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object SoulsBlessing : MageAbility {
	override val id = "souls_blessing"
	override val magicPath = MagicPath.NECROMANCY
	override val minimumMageLevel: Int = 3
	override val displayName: String = "Souls Blessing"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
