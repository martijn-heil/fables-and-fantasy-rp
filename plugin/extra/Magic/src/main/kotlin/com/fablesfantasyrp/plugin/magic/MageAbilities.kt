package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.ability.aeromancy.Cloud
import com.fablesfantasyrp.plugin.magic.ability.aeromancy.lunarmancy.CleansingTouch
import com.fablesfantasyrp.plugin.magic.ability.aeromancy.tempestacy.LightningReflexes
import com.fablesfantasyrp.plugin.magic.ability.aquamancy.PurifyingProwess
import com.fablesfantasyrp.plugin.magic.ability.aquamancy.frigumancy.IcyMold
import com.fablesfantasyrp.plugin.magic.ability.aquamancy.marinamancy.AquaticBlessing
import com.fablesfantasyrp.plugin.magic.ability.geomancy.EarthlyAwareness
import com.fablesfantasyrp.plugin.magic.ability.geomancy.biomancy.CalmingAura
import com.fablesfantasyrp.plugin.magic.ability.geomancy.petromancy.CrystallineHands
import com.fablesfantasyrp.plugin.magic.ability.hemomancy.IchorFriend
import com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofbeast.*
import com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofblood.BloodFlame
import com.fablesfantasyrp.plugin.magic.ability.hemomancy.houseofnight.*
import com.fablesfantasyrp.plugin.magic.ability.necromancy.SoulsBlessing
import com.fablesfantasyrp.plugin.magic.ability.necromancy.deathdestined.NecromanticResistance
import com.fablesfantasyrp.plugin.magic.ability.necromancy.souldestined.Fragmentation
import com.fablesfantasyrp.plugin.magic.ability.pyromancy.FlamingFamiliar
import com.fablesfantasyrp.plugin.magic.ability.pyromancy.magmamancy.RunningHot
import com.fablesfantasyrp.plugin.magic.ability.pyromancy.solarmancy.BrilliantRadiance
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath

object MageAbilities {
	private val data = listOf(
			CleansingTouch,
			LightningReflexes,
			Cloud,
			IcyMold,
			AquaticBlessing,
			PurifyingProwess,
			CalmingAura,
			CrystallineHands,
			EarthlyAwareness,
			ApexInstinct,
			Claws,
			FightOrFlight,
			PreyingScents,
			ProtectiveCoat,
			BloodFlame,
			BloodSucker,
			BloodyNails,
			CrimsonEyes,
			VampiricHover,
			VampiricSkin,
			IchorFriend,
			RunningHot,
			BrilliantRadiance,
			FlamingFamiliar,
			NecromanticResistance,
			Fragmentation,
			SoulsBlessing
	).groupBy { it.magicPath }.mapValues { it.value.toSet() }

	val all = data.values.flatten()
	fun forPath(magicPath: MagicPath): Collection<MageAbility> {
		val all = ArrayList<MageAbility>()
		data[magicPath]?.let { all.addAll(it) }
		data[magicPath.basePath]?.let { all.addAll(it) }
		return all.distinct()
	}
	fun forId(id: String) = all.find { it.id == id }
}
