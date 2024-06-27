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
package com.fablesfantasyrp.plugin.bell.domain.entity

import com.fablesfantasyrp.plugin.bell.PLUGIN
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.model.Named
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import com.github.shynixn.mccoroutine.bukkit.launch
import dev.kord.common.entity.Snowflake
import kotlinx.coroutines.delay
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import java.time.Duration
import java.time.Instant
import kotlin.random.Random

class Bell : DataEntity<Int, Bell>, Named {
	override var dirtyMarker: DirtyMarker<Bell>? = null
	override val id: Int

	var isDestroyed: Boolean = false

	var isRinging = false
		private set
	val location: BlockIdentifier
	var discordChannelId: Snowflake		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var discordRoleIds: Set<Snowflake>	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var locationName: String 			set(value) { if (field != value) { val oldValue = field; field = value; dirtyMarker?.markDirty(this, "name", oldValue, value) } }
	override val name get() = locationName

	fun ringOnce() {
		val range = 512
		val volume = (range / 16).toFloat()
		val sound = Sound.sound(
			Key.key("fablesfantasyrp", "ambient.custom.church_bell"),
			Sound.Source.AMBIENT,
			volume,
			1.0f
		)
		Bukkit.getWorld(location.world)!!.playSound(sound, location.x.toDouble(), location.y.toDouble(), location.z.toDouble())
	}

	fun ringFor(duration: Duration) {
		check(!isRinging)
		isRinging = true
		PLUGIN.launch {
			val start = Instant.now()
			while (Duration.between(start, Instant.now()).seconds < duration.seconds) {
				ringOnce()
				delay(Random.nextInt(1000, 1500).toLong())
			}
			isRinging = false
		}
	}

	constructor(id: Int,
				location: BlockIdentifier,
				locationName: String,
				discordChannelId: Snowflake,
				discordRoleIds: Set<Snowflake>,
				dirtyMarker: DirtyMarker<Bell>? = null) {
		this.id = id
		this.location = location
		this.locationName = locationName
		this.discordChannelId = discordChannelId
		this.discordRoleIds = discordRoleIds

		this.dirtyMarker = dirtyMarker // Must be last
	}
}
