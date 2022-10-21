package com.fablesfantasyrp.plugin.magic.ability.pyromancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object FlamingFamiliar : MageAbility {
	override val id = "flaming_familiar"
	override val magicPath = MagicPath.PYROMANCY
	override val displayName: String = "Flaming Familiar"
	override val description: String =
			"The mage is capable of creating a small, flame-like spirit, which follows them around. " +
			"While being near their creator the spirit will grant them +1 to either their casting roll or their success roll. " +
			"However, the familiar can’t be used in combat and can't ignite anything. "

	override fun applyTo(mage: Mage) {

	}
}
