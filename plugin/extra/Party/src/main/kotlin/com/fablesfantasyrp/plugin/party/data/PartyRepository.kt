package com.fablesfantasyrp.plugin.party.data

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.party.data.entity.Party

interface PartyRepository : MutableRepository<Party>, KeyedRepository<Int, Party> {
	fun forMember(character: Character): Party?
	fun allNames(): Collection<String>
	fun forName(name: String): Party?
}
