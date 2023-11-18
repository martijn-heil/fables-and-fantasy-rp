package com.fablesfantasyrp.plugin.magic.ability.aquamancy

import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object PurifyingProwess : MageAbility {
	override val id = "purifying_prowess"
	override val magicPath = MagicPath.AQUAMANCY
	override val minimumMageLevel: Int = 3
	override val displayName: String = "Purifying Prowess"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}