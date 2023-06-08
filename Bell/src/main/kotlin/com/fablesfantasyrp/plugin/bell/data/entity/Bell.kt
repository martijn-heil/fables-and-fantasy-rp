package com.fablesfantasyrp.plugin.bell.data.entity

import com.fablesfantasyrp.plugin.bell.PLUGIN
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.Named
import com.fablesfantasyrp.plugin.utils.BlockIdentifier
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
		PLUGIN.launch {
			val start = Instant.now()
			while (Duration.between(start, Instant.now()).seconds < duration.seconds) {
				ringOnce()
				delay(Random.nextInt(1000, 1500).toLong())
			}
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
