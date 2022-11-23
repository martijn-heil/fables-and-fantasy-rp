package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Gender
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import java.time.Instant

class Character : DataEntity<ULong, Character>, CharacterData {
	val playerInstance: PlayerInstance

	val createdAt: Instant?

	override var name: String
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val player: OfflinePlayer get() = throw NotImplementedError()

	override var age: UInt 				set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var description: String 	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var gender: Gender 		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var race: Race 			set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var stats: CharacterStats 	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var money: ULong
		get() = throw NotImplementedError()
		set(value) { throw NotImplementedError() }

	override var location: Location
		get() = playerInstance.location
		set(value) { playerInstance.location = value }

	override val id: ULong
	override var dirtyMarker: DirtyMarker<Character>? = null

	constructor(id: ULong,
				playerInstance: PlayerInstance,
				name: String,
				race: Race,
				gender: Gender,
				stats: CharacterStats,
				age: UInt,
				description: String,
				createdAt: Instant? = Instant.now()) {
		this.id = id
		this.playerInstance = playerInstance
		this.name = name
		this.race = race
		this.gender = gender
		this.stats = stats
		this.age = age
		this.description = description
		this.location = location
		this.createdAt = createdAt
	}
}
