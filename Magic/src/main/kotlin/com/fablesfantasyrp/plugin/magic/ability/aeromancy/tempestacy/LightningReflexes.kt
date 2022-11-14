package com.fablesfantasyrp.plugin.magic.ability.aeromancy.tempestacy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object LightningReflexes : MageAbility {
	override val id = "lightning_reflexes"
	override val magicPath = MagicPath.TEMPESTACY
	override val displayName: String = "Lightning Reflexes"
	override val description: String =
			"When entering combat, the wind mage of the highest level always goes first, " +
					"except if a 1 was rolled in a major fight. If there are several air mages in one combat session, " +
					"they will all go before the other people in order of what was rolled. " +
					"This also makes the aeromancer’s common reflexes better, which can be used in roleplay situations. "

	override fun applyTo(mage: Mage) {

	}
}
