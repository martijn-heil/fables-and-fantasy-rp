package com.fablesfantasyrp.plugin.characters.database

import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterRepository
import com.fablesfantasyrp.plugin.characters.playerCharacterRepository
import com.fablesfantasyrp.plugin.characters.CharacterStats
import com.fablesfantasyrp.plugin.characters.Gender
import com.fablesfantasyrp.plugin.characters.PlayerCharacter
import com.fablesfantasyrp.plugin.characters.Race
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import org.bukkit.Location
import org.bukkit.OfflinePlayer

class DatabasePlayerCharacter : PlayerCharacter {
	private var dirtyMarker: DirtyMarker<DatabasePlayerCharacter>? = null
	override val id: ULong
	override var name: String
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var age: UInt
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var description: String
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var location: Location
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var money: ULong
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val player: OfflinePlayer
	override val gender: Gender
	override val race: Race
	override val stats: CharacterStats

	internal constructor(dirtyMarker: DirtyMarker<DatabasePlayerCharacter>?, id: ULong, name: String, age: UInt,
						 description: String, gender: Gender, race: Race, stats: CharacterStats,
						 location: Location, money: ULong, player: OfflinePlayer) {
		this.id = id
		this.name = name
		this.age = age
		this.description = description
		this.gender = gender
		this.race = race
		this.stats = stats
		this.location = location
		this.money = money
		this.player = player
		this.dirtyMarker = dirtyMarker // Important that this is last
	}

	override fun equals(other: Any?): Boolean {
		return if (other is DatabasePlayerCharacter) {
			other.id == id
		} else false
	}

	override fun hashCode(): Int {
		return id.hashCode()
	}

	companion object : PlayerCharacterRepository by playerCharacterRepository
}
