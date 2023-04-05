package com.fablesfantasyrp.plugin.magic.ability.necromancy.souldestined

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object Fragmentation : MageAbility {
	override val id = "fragmentation"
	override val magicPath = MagicPath.NECROMANCY_SOUL_DESTINED
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Fragmentation"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
