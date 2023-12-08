package com.fablesfantasyrp.plugin.magic.dal.repository

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.magic.dal.model.MageData

interface MageDataRepository : MutableRepository<MageData>, KeyedRepository<Long, MageData> {
	fun forCharacter(characterId: Int): MageData?
	fun forCharacterOrCreate(characterId: Int): MageData
}
