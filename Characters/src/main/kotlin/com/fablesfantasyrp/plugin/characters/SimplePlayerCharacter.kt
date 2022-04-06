package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import org.bukkit.Location
import org.bukkit.OfflinePlayer

class SimplePlayerCharacter : PlayerCharacter {
	private var dirtyMarker: DirtyMarker<SimplePlayerCharacter>? = null
	override val id: ULong
	override var name: String
		set(value) { field = value; dirtyMarker?.markDirty(this) }

	override var age: UInt
		set(value) { field = value; dirtyMarker?.markDirty(this) }

	override var description: String
		set(value) { field = value; dirtyMarker?.markDirty(this) }

	override var location: Location
		set(value) { field = value; dirtyMarker?.markDirty(this) }

	override var money: ULong
		set(value) { field = value; dirtyMarker?.markDirty(this) }

	override val player: OfflinePlayer
	override val gender: Gender
	override val race: Race
	override val stats: CharacterStats

	internal constructor(dirtyMarker: DirtyMarker<SimplePlayerCharacter>?, id: ULong, name: String, age: UInt,
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
		return if (other is SimplePlayerCharacter) {
			other.id == id
		} else false
	}

	override fun hashCode(): Int {
		return id.hashCode()
	}
}
