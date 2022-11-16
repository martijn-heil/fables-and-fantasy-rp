package com.fablesfantasyrp.plugin.magic.ability.geomancy.biomancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object CalmingAura : MageAbility {
	override val id = "calming_aura"
	override val magicPath = MagicPath.BIOMANCY
	override val displayName: String = "Calming Aura"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
