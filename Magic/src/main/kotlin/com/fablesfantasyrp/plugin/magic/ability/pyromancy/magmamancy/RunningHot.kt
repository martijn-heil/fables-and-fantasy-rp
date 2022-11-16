package com.fablesfantasyrp.plugin.magic.ability.pyromancy.magmamancy

import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.data.entity.Mage

object RunningHot : MageAbility {
	override val id = "running_hot"
	override val magicPath = MagicPath.MAGMAMANCY
	override val displayName: String = "Running Hot!"
	override val description: String = "When the spellcaster is in CRP they feel the thrill of battle and channel heat from deep within them, " +
			"willing or not. This creates an aura of scorching heat in a radius of 2 meters around them for D3 + (Levels above Level 8)turns.\n" +
			"\n" +
			"This meaning, the ability duration is as follows:\n" +
			"\n" +
			"    At level 8, D3\n" +
			"    At level 9, D3+1\n" +
			"    At level 10, D3+2\n" +
			"\n" +
			"This heat is unbearable to anyone who gets too close while unprepared. " +
			"Those who dare get close to the caster will take D1 damage per turn they are in range of the heat wave. " +
			"Once the wave dies down it will not activate itself again until after a duration of 10 turns. "

	override fun applyTo(mage: Mage) {

	}
}
