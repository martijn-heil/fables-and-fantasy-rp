package com.fablesfantasyrp.plugin.magic.ability.aquamancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object PurifyingProwess : MageAbility {
	override val id = "purifying_prowess"
	override val magicPath = MagicPath.AQUAMANCY
	override val displayName: String = "Purifying Prowess"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
