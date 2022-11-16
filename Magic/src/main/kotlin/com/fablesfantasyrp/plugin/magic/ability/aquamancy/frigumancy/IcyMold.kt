package com.fablesfantasyrp.plugin.magic.ability.aquamancy.frigumancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object IcyMold : MageAbility {
	override val id = "icy_mold"
	override val magicPath = MagicPath.FRIGUMANCY
	override val displayName: String = "Icy Mold"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
