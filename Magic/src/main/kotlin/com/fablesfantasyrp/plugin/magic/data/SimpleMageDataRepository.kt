package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface SimpleMageDataRepository : MutableRepository<SimpleMageData>, KeyedRepository<Long, SimpleMageData> {
	fun forPlayerCharacter(playerCharacter: PlayerCharacterData): SimpleMageData
}
