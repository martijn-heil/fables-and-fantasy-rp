package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Gender
import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import org.bukkit.Location
import org.bukkit.OfflinePlayer

class PlayerCharacter : DataEntity<ULong, PlayerCharacter>, PlayerCharacterData {
	override var name: String
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val player: OfflinePlayer

	override var age: UInt
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var description: String
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val gender: Gender

	override var race: Race
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var stats: CharacterStats
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var location: Location
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var money: ULong
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val id: ULong
	override var dirtyMarker: DirtyMarker<PlayerCharacter>? = null

	constructor(id: ULong, player: OfflinePlayer, name: String, race: Race, gender: Gender,
				stats: CharacterStats, money: ULong, age: UInt, description: String, location: Location) {
		this.id = id
		this.player = player
		this.name = name
		this.race = race
		this.gender = gender
		this.stats = stats
		this.money = money
		this.age = age
		this.description = description
		this.location = location
	}
}
