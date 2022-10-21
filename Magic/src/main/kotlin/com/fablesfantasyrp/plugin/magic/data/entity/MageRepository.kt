package com.fablesfantasyrp.plugin.magic.data.entity

import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface MageRepository : MutableRepository<Mage>, KeyedRepository<Long, Mage> {
	fun forPlayerCharacter(c: PlayerCharacterData): Mage?
	fun forPlayerCharacterOrCreate(c: PlayerCharacterData): Mage
}
