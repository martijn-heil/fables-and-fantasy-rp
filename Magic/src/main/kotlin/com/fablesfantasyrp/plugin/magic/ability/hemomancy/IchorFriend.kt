package com.fablesfantasyrp.plugin.magic.ability.hemomancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object IchorFriend : MageAbility {
	override val id = "ichor_friend"
	override val magicPath = MagicPath.HEMOMANCY
	override val minimumMageLevel: Int = 3
	override val displayName: String = "Ichor Friend"
	override val description: String = ""

	override fun applyTo(mage: Mage) {

	}
}
