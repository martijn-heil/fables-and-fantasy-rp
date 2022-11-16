package com.fablesfantasyrp.plugin.magic.ability.pyromancy.solarmancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object BrilliantRadiance : MageAbility {
	override val id = "brilliant_radiance"
	override val magicPath = MagicPath.SOLARMANCY
	override val displayName: String = "Brilliant Radiance"
	override val description: String = "The mage slowly consumes the sun's heat and energy throughout the battle, " +
			"charging themselves with an unimaginable tension. " +
			"After at least one round of charging, " +
			"the spellcaster can use their action to release the tension from inside themselves, " +
			"blinding everyone in a range of 10 meters for D4+1 rounds with a varying debuff.\n" +
			"\n" +
			"The strength of blindness depends on how many turns the mage charged this ability:\n" +
			"\n" +
			"    One turn: A debuff of -1\n" +
			"    Two turns: A debuff of -2\n" +
			"    ...\n" +
			"\n" +
			"Once a spellcaster reaches level 5 of Solarmancy, " +
			"their intake of sunlight vastly increases which grants any of their blindness debuffs to be stronger by one point. "

	override fun applyTo(mage: Mage) {

	}
}
