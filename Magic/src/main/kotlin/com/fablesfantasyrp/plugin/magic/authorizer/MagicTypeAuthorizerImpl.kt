package com.fablesfantasyrp.plugin.magic.authorizer

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicType
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository

class MagicTypeAuthorizerImpl(private val mages: MageRepository,
							  private val spellAuthorizer: SpellAuthorizer) : MagicTypeAuthorizer {
	override fun getMagicTypes(character: Character): Set<MagicType> {
		val mage = mages.forCharacter(character)

		val magicTypes = spellAuthorizer.getSpells(character).map { it.magicPath.magicType }.toMutableSet()
		if (mage != null) magicTypes.add(mage.magicPath.magicType)

		return magicTypes
	}
}
