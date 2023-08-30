package com.fablesfantasyrp.plugin.magic.authorizer

import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.charactertraits.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.KnownSpells
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository

class SpellAuthorizerImpl(private val mages: MageRepository,
						  private val spells: SpellDataRepository,
						  private val traits: CharacterTraitRepository) : SpellAuthorizer {
	override fun getSpells(character: Character): Set<SpellData> {
		val availableSpells = HashSet<SpellData>()

		val mage = mages.forCharacter(character)
		if (mage != null) {
			availableSpells.addAll(mage.spells)
		}

		fun hasTrait(id: String) = traits.hasTrait(character, id)
		fun addSpell(id: String) = spells.forId(id)?.let { availableSpells.add(it) }

		if (hasTrait(KnownCharacterTraits.ARCANE_INITIATE)) addSpell(KnownSpells.FLAME_ARROW)
		if (hasTrait(KnownCharacterTraits.DRAGON_BLOODED)) addSpell(KnownSpells.BREATH_OF_THE_DRAGON)
		if (hasTrait(KnownCharacterTraits.ASPECT_OF_LILITHS_VEIL)) addSpell(KnownSpells.ICE_MAIDENS_KISS)
		if (hasTrait(KnownCharacterTraits.PROPHET_OF_THE_GREEN_FLAME)) addSpell(KnownSpells.FIREBALL)

		return availableSpells
	}

	override fun hasSpell(character: Character, id: String): Boolean = getSpells(character).contains(spells.forId(id))
	override fun hasSpell(character: Character, spell: SpellData): Boolean = getSpells(character).contains(spell)
}
