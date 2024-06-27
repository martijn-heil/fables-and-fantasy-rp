/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.party.data

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository
import com.fablesfantasyrp.plugin.party.data.entity.Party

class MapPartyRepository : SimpleMapRepository<Int, Party>(), PartyRepository, DirtyMarker<Party> {
	private var idCounter = 0
	private val byMember: MutableMap<Int, Party> = HashMap()
	private val members: MutableMap<Party, Collection<Int>> = HashMap()

	override fun forMember(character: Character): Party? {
		return byMember[character.id]
	}

	override fun allNames(): Collection<String> {
		return all().map { it.name }
	}

	override fun forName(name: String): Party? {
		return all().find { it.name == name }
	}

	override fun create(v: Party): Party {
		val party = Party(
			id = ++idCounter,
			owner = v.owner,
			members = v.members,
			name = v.name,
			color = v.color,
			dirtyMarker = this
		)
		val result = super.create(party)
		result.members.forEach { byMember[it.id] = result }
		members[result] = result.members.map { it.id }
		return result
	}

	override fun destroy(v: Party) {
		super.destroy(v)
		v.members.forEach { byMember.remove(it.id) }
		members.remove(v)
		v.isDestroyed = true
	}

	override fun markDirty(v: Party) {

	}

	override fun markDirty(v: Party, what: String) {
		when(what) {
			"members" -> {
				val newMembers = v.members.map { it.id }
				val removedMembers = members[v]?.filter { !newMembers.contains(it) }
				removedMembers?.forEach { byMember.remove(it) }
				newMembers.forEach { byMember[it] = v }
				members[v] = newMembers
			}
			else -> markDirty(v)
		}
	}
}
