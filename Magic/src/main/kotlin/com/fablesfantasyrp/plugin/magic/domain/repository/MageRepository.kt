package com.fablesfantasyrp.plugin.magic.domain.repository

import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

interface MageRepository : MutableRepository<Mage>, KeyedRepository<Long, Mage> {
	fun forCharacter(c: Character): Mage?
	fun forCharacterOrCreate(c: Character): Mage
}
