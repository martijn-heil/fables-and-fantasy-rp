package com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofbeast

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object PreyingScents : MageAbility {
	override val id = "preying_scents"
	override val magicPath = MagicPath.HEMOMANCY_HOUSE_OF_BEAST
	override val displayName: String = "Preying Scents"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
