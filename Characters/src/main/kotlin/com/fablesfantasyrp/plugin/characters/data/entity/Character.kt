package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.characters.data.*
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.inventory.inventory
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.Services
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import java.time.Instant

class Character : DataEntity<Int, Character>, CharacterData {
	val profile: Profile

	val createdAt: Instant?
	var lastSeen: Instant?
		get() = if (Services.get<ProfileManager>().getCurrentForProfile(profile) != null) Instant.now() else field

	var diedAt: Instant?
	var shelvedAt: Instant?

	var isDead: Boolean
		set(value) {
			if (field == value) return

			if (value) {
				diedAt = Instant.now()
				val profile = this.profile
				val player = Services.get<ProfileManager>().getCurrentForProfile(profile)
				if (player != null) {
					player.health = 0.0
					player.spigot().respawn()
				}
				val inventory = profile.inventory
				inventory.inventory.clear()
				inventory.enderChest.clear()
			} else {
				diedAt = null
			}

			field = value
			profile.isActive = !(isShelved || isDead)
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
			profile.isActive = !(isShelved || isDead)
			dirtyMarker?.markDirty(this)
		}

	override var name: String
		set(value) {
			if (field == value) return
			field = value
			dirtyMarker?.markDirty(this)
			profile.description = value
		}

	@Deprecated("Not implemented")
	override val player: OfflinePlayer get() = throw NotImplementedError()

	override var age: UInt 				set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var description: String 	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var gender: Gender 		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var race: Race 			set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var stats: CharacterStats 	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	val totalStats: CharacterStats get() = stats + race.boosters + CHARACTER_STATS_FLOOR

	override var money: ULong
		get() = throw NotImplementedError() // See FablesEconomy based on Profile instead
		set(value) { throw NotImplementedError() }

	override var location: Location
		get() = profile.location
		set(value) { profile.location = value }

	override val id: Int
	override var dirtyMarker: DirtyMarker<Character>? = null

	constructor(id: Int,
				profile: Profile,
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
		this.profile = profile
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
