package com.fablesfantasyrp.plugin.magic.authorizer

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicType

interface MagicTypeAuthorizer {
	fun getMagicTypes(character: Character): Set<MagicType>
}
