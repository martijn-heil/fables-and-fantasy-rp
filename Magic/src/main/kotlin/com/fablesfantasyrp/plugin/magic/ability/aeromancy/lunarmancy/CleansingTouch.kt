package com.fablesfantasyrp.plugin.magic.ability.aeromancy.lunarmancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object CleansingTouch : MageAbility {
	override val id = "cleansing_touch"
	override val magicPath = MagicPath.LUNARMANCY
	override val minimumMageLevel: Int = 8
	override val displayName: String = "Cleansing Touch"
	override val description: String =
			"Lunarmancers can cleanse corruption of objects and people. " +
			"This means they can heal people tainted by voidal energies and can heal voidal mages. " +
			"A Lunarmancer can decrease a voidal mages' level by one per OOC week until they are cleansed of voidal energies. " +
			"This can only happen if the voidal mage allows this to happen. "

	override fun applyTo(mage: Mage) {

	}
}
