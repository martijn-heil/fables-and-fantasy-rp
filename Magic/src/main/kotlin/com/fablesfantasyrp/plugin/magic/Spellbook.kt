package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.magic.data.SpellData

interface Spellbook {
	val spells: Set<SpellData>
}
