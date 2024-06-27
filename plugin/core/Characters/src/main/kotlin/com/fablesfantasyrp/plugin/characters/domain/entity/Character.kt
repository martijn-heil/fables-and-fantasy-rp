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
package com.fablesfantasyrp.plugin.characters.domain.entity

import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.characters.SYSPREFIX
import com.fablesfantasyrp.plugin.characters.calculateDateOfNaturalDeath
import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.dal.enums.Gender
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.CHARACTER_STATS_FLOOR
import com.fablesfantasyrp.plugin.characters.domain.CharacterStats
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.flaunch
import com.fablesfantasyrp.plugin.characters.modifiers.health.HealthModifier
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.inventory.domain.repository.ProfileInventoryRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.time.formatDateLong
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import com.fablesfantasyrp.plugin.utils.Services
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.koin.core.context.GlobalContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.max

class Character : DataEntity<Int, Character> {
	val profile: Profile

	val createdAt: Instant?
	var lastSeen: Instant?
		get() = if (Services.get<ProfileManager>().getCurrentForProfile(profile) != null) Instant.now() else field
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	var diedAt: Instant? 						set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var shelvedAt: Instant? 					set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var changedStatsAt: Instant? 				set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var dateOfNaturalDeath: FablesLocalDate? 	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var dateOfBirth: FablesLocalDate?
		set(value) {
			if (field != value) {
				field = value
				dirtyMarker?.markDirty(this)
				dateOfNaturalDeath = value?.let { if (race.medianAge != null) calculateDateOfNaturalDeath(it, race.medianAge!!) else null }
				checkNaturalDeath()
			}
		}

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
					//player.showEndCredits()
				}
				flaunch {
					val profileInventory = Services.get<ProfileInventoryRepository>().forOwner(profile)
					profileInventory.inventory.clear()
					profileInventory.enderChest.clear()
				}
			} else {
				diedAt = null
			}

			field = value
			if (field) this.isShelved = false
			profile.isActive = !(isShelved || field)
			dirtyMarker?.markDirty(this)
		}
		get() {
			checkNaturalDeath()
			return field
		}

	val isDying: Boolean
		get() = !isDead && dateOfNaturalDeath != null && FablesLocalDate.now().until(dateOfNaturalDeath!!).toTotalMonths() < 6

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

	var name: String
		set(value) {
			if (field == value) return
			field = value
			dirtyMarker?.markDirty(this)
			profile.description = value
			val player = Services.get<ProfileManager>().getCurrentForProfile(profile)
			player?.dFlags?.setFlag("characters_name", ElementTag(name), null)
		}

	val age: UInt? get() {
		val now = FablesLocalDate.now()
		val dateOfNaturalDeath = this.dateOfNaturalDeath
		val compareTo = if (dateOfNaturalDeath != null && (now == dateOfNaturalDeath || now.isAfter(dateOfNaturalDeath))) {
			dateOfNaturalDeath
		} else {
			now
		}

		return dateOfBirth?.until(compareTo, ChronoUnit.YEARS)?.toUInt()
	}
	var description: String 	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var gender: Gender set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var race: Race set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	val totalStats: CharacterStats
		get() = stats + CHARACTER_STATS_FLOOR.withModifiers(
		GlobalContext.get().getAll<StatsModifier>().map { it.calculateModifiers(this) }
	)
	var stats: CharacterStats
		set(value) {
			if (field != value) {
				require(value.agility < 128U)
				require(value.strength < 128U)
				require(value.defense < 128U)
				require(value.intelligence < 128U)
				field = value
				dirtyMarker?.markDirty(this)
			}
		}

	var traits: Set<CharacterTrait>
		set(value) {
			field = value.toHashSet()
			dirtyMarker?.markDirty(this)
		}

	val maximumHealth: UInt get() =
		max(0, (12 + CharacterStatKind.STRENGTH.getRollModifierFor(totalStats.strength) +
			GlobalContext.get().getAll<HealthModifier>().sumOf { it.calculateModifier(this) })).toUInt()

	fun checkNaturalDeath() {
		val today = FablesLocalDate.now()
		if (dateOfNaturalDeath != null && (today == dateOfNaturalDeath || today.isAfter(dateOfNaturalDeath!!))) {
			Services.get<ProfileManager>().getCurrentForProfile(profile)
				?.sendMessage(miniMessage.deserialize("<prefix> <red>" +
					"<bold><name> has died of old age on <underlined><date></underlined>.</bold></red>",
					Placeholder.component("prefix", legacyText(SYSPREFIX)),
					Placeholder.unparsed("date", formatDateLong(dateOfNaturalDeath!!)),
					Placeholder.unparsed("name", name)
				))
			this.isDead = true
		}
	}

	override val id: Int
	override var dirtyMarker: DirtyMarker<Character>? = null

	constructor(id: Int,
				profile: Profile,
				name: String,
				race: Race,
				gender: Gender,
				stats: CharacterStats,
				description: String,
				lastSeen: Instant? = null,
				createdAt: Instant? = Instant.now(),
				isDead: Boolean = false,
				dateOfBirth: FablesLocalDate?,
				dateOfNaturalDeath: FablesLocalDate?,
				diedAt: Instant? = null,
				isShelved: Boolean = false,
				shelvedAt: Instant? = null,
				changedStatsAt: Instant? = null,
				traits: Set<CharacterTrait> = emptySet(),
				dirtyMarker: DirtyMarker<Character>? = null) {
		this.id = id
		this.profile = profile
		this.name = name
		this.race = race
		this.gender = gender
		this.stats = stats
		this.description = description
		this.lastSeen = lastSeen
		this.createdAt = createdAt
		this.dateOfBirth = dateOfBirth
		if (dateOfNaturalDeath != null) {
			this.dateOfNaturalDeath = dateOfNaturalDeath
		} else {
			this.dateOfNaturalDeath = dateOfBirth?.let { if (race.medianAge != null) calculateDateOfNaturalDeath(it, race.medianAge!!) else null }
		}
		this.isDead = isDead
		this.isShelved = isShelved
		this.diedAt = diedAt
		this.shelvedAt = shelvedAt
		this.changedStatsAt = changedStatsAt
		this.traits = traits

		this.dirtyMarker = dirtyMarker

		this.checkNaturalDeath()
	}
}
