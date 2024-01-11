package com.fablesfantasyrp.plugin.magic.domain.entity

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.domain.DISTANCE_TALK
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicType
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.EndGateway

class Tear : DataEntity<Long, Tear> {
	var isDeleted = false
		set(value) {
			if (value) this.despawn()
			field = value
		}

	override var dirtyMarker: DirtyMarker<Tear>? = null

	override val id: Long

	var magicType: MagicType
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	var owner: Character
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	var location: Location
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	constructor(id: Long,
				location: Location,
				magicType: MagicType,
				owner: Character,
				dirtyMarker: DirtyMarker<Tear>? = null) : super() {
		this.id = id
		this.location = location
		this.magicType = magicType
		this.owner = owner
		this.dirtyMarker = dirtyMarker
		this.spawn()
	}

	fun despawn() {
		location.block.type = Material.AIR
		getPlayersWithinRange(location, DISTANCE_TALK).forEach {
			it.playSound(Sound.sound(Key.key("minecraft", "entity.enderman.teleport"),
					Sound.Source.AMBIENT, 1.0f, 1.0f))
		}
	}

	fun spawn() {
		location.block.type = Material.END_GATEWAY
		val blockState = location.block.state as EndGateway
		blockState.age = Long.MAX_VALUE // This will effectively delay the beam effect for ages
		blockState.update(true)
		getPlayersWithinRange(location, DISTANCE_TALK).forEach {
			it.playSound(Sound.sound(Key.key("minecraft", "entity.enderman.teleport"),
					Sound.Source.AMBIENT, 1.0f, 1.0f))
		}
	}
}
