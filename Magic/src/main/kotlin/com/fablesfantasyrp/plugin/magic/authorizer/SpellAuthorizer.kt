package com.fablesfantasyrp.plugin.magic.authorizer

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData

interface SpellAuthorizer {
	fun getSpells(character: Character): Set<SpellData>
	fun hasSpell(character: Character, id: String): Boolean
	fun hasSpell(character: Character, spell: SpellData): Boolean
}
