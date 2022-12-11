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
import com.fablesfantasyrp.plugin.playerinstance.currentPlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.utils.essentialsSpawn
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import java.time.Instant

class Character : DataEntity<ULong, Character>, CharacterData {
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
					player.currentPlayerInstance = null
				}
				val inventory = playerInstance.inventory
				inventory.inventory.clear()
				inventory.enderChest.clear()
				playerInstance.location = essentialsSpawn.getSpawn("default").toCenterLocation()
			} else {
				diedAt = null
			}

			field = value
			dirtyMarker?.markDirty(this)
		}

	var isShelved: Boolean
		set(value) {
			if (field == value) return

			if (value) {
				shelvedAt = Instant.now()
				val playerInstance = this.playerInstance
				val player = playerInstance.currentPlayer
				if (player != null) {
					player.currentPlayerInstance = null
					player.teleport(essentialsSpawn.getSpawn("defualt").toCenterLocation())
				}
			} else {
				shelvedAt = null
			}

			field = value
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
		this.location = location
		this.lastSeen = lastSeen
		this.createdAt = createdAt
		this.isDead = isDead
		this.isShelved = isShelved
		this.diedAt = diedAt
		this.shelvedAt = shelvedAt

		this.dirtyMarker = dirtyMarker
	}
}
