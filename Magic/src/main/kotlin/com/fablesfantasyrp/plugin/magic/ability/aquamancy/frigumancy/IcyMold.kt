package com.fablesfantasyrp.plugin.magic.ability.aquamancy.frigumancy

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object IcyMold : MageAbility {
	override val id = "icy_mold"
	override val magicPath = MagicPath.FRIGUMANCY
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Icy Mold"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
