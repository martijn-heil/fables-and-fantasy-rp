package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Gender
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.inventory.inventory
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.playerinstance.currentPlayer
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import java.time.Instant

class Character : DataEntity<Int, Character>, CharacterData {
	val playerInstance: PlayerInstance

	val createdAt: Instant?
	var lastSeen: Instant?
		get() = if (playerInstance.currentPlayer != null) Instant.now() else field

	var diedAt: Instant?
	var shelvedAt: Instant?

	var isDead: Boolean
		set(value) {
			if (field == value) return

			if (value) {
				diedAt = Instant.now()
				val playerInstance = this.playerInstance
				val player = playerInstance.currentPlayer
				if (player != null) {
					player.health = 0.0
					player.spigot().respawn()
				}
				val inventory = playerInstance.inventory
				inventory.inventory.clear()
				inventory.enderChest.clear()
			} else {
				diedAt = null
			}

			field = value
			playerInstance.isActive = !(isShelved || isDead)
			dirtyMarker?.markDirty(this)
		}

	var isShelved: Boolean
		set(value) {
			if (field == value) return

			shelvedAt = if (value) {
				Instant.now()
			} else {
				null
			}

			field = value
			playerInstance.isActive = !(isShelved || isDead)
			dirtyMarker?.markDirty(this)
		}

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

	override val id: Int
	override var dirtyMarker: DirtyMarker<Character>? = null

	constructor(id: Int,
				playerInstance: PlayerInstance,
				name: String,
				race: Race,
				gender: Gender,
				stats: CharacterStats,
				age: UInt,
				description: String,
				lastSeen: Instant? = null,
				createdAt: Instant? = Instant.now(),
				isDead: Boolean = false,
				diedAt: Instant? = null,
				isShelved: Boolean = false,
				shelvedAt: Instant? = null,
				dirtyMarker: DirtyMarker<Character>? = null) {
		this.id = id
		this.playerInstance = playerInstance
		this.name = name
		this.race = race
		this.gender = gender
		this.stats = stats
		this.age = age
		this.description = description
		this.lastSeen = lastSeen
		this.createdAt = createdAt
		this.isDead = isDead
		this.isShelved = isShelved
		this.diedAt = diedAt
		this.shelvedAt = shelvedAt

		this.dirtyMarker = dirtyMarker
	}
}
