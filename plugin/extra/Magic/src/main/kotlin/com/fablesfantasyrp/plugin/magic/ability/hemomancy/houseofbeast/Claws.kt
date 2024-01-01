package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofbeast

import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object Claws : MageAbility {
	override val id = "claws"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_BEAST
	override val minimumMageLevel: Int = 7
	override val displayName: String = "Claws"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
