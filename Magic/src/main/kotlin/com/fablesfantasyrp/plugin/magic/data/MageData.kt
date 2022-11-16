package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.magic.ability.MageAbility
import com.fablesfantasyrp.plugin.magic.MagicPath
import com.fablesfantasyrp.plugin.magic.getSpellCastingBonus

interface MageData : Identifiable<Long> {
	override var id: Long
	val magicLevel: Int
	val magicPath: MagicPath
	val spells: List<SpellData>
	var activeAbilities: Set<MageAbility>

	val spellCastingBonus: UInt get() = getSpellCastingBonus(this.magicPath, this.magicLevel).toUInt()
}
