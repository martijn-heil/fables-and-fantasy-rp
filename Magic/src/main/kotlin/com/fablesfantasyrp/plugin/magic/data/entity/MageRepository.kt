package com.fablesfantasyrp.plugin.magic.data.entity

import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface MageRepository : MutableRepository<Mage>, KeyedRepository<Long, Mage> {
	fun forPlayerCharacter(c: CharacterData): Mage?
	fun forPlayerCharacterOrCreate(c: CharacterData): Mage
}
