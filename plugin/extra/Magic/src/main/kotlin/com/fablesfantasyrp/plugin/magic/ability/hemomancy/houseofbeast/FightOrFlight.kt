package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofbeast

import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

object FightOrFlight : MageAbility {
	override val id = "fight_or_flight"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_BEAST
	override val minimumMageLevel: Int = 9
	override val displayName: String = "Fight or Flight"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
