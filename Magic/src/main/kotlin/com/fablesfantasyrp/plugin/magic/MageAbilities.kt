package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.magic.ability.aeromancy.Cloud
import com.fablesfantasyrp.plugin.magic.ability.aeromancy.lunarmancy.CleansingTouch
import com.fablesfantasyrp.plugin.magic.ability.aeromancy.tempestacy.LightningReflexes
import com.fablesfantasyrp.plugin.magic.ability.pyromancy.FlamingFamiliar
import com.fablesfantasyrp.plugin.magic.ability.pyromancy.RunningHot
import com.fablesfantasyrp.plugin.magic.ability.pyromancy.solarmancy.BrilliantRadiance

object MageAbilities {
	private val data = listOf(Cloud,
			LightningReflexes,
			CleansingTouch,
			BrilliantRadiance,
			FlamingFamiliar,
			RunningHot).groupBy { it.magicPath }.mapValues { it.value.toSet() }

	val all = data.values.flatten()
	fun forPath(magicPath: MagicPath) = data[magicPath]
}
