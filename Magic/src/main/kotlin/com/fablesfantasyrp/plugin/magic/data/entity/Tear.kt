package com.fablesfantasyrp.plugin.magic.data.entity

import com.fablesfantasyrp.plugin.chat.getPlayersWithinRange
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.magic.MagicType
import com.fablesfantasyrp.plugin.magic.data.TearData
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.EndGateway

class Tear : TearData, HasDirtyMarker<Tear> {
	private val server
		get() = Bukkit.getServer()

	var isDeleted = false
		set(value) {
			if (value) this.despawn()
			field = value
		}

	override var dirtyMarker: DirtyMarker<Tear>? = null

	override var id: Long
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var magicType: MagicType
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var owner: Mage
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var location: Location
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	constructor(id: Long, location: Location, magicType: MagicType, owner: Mage) : super() {
		this.id = id
		this.location = location
		this.magicType = magicType
		this.owner = owner
		this.spawn()
	}

	fun despawn() {
		location.block.type = Material.AIR
		getPlayersWithinRange(location, 15U).forEach {
			it.playSound(Sound.sound(Key.key("minecraft", "entity.enderman.teleport"),
					Sound.Source.AMBIENT, 1.0f, 1.0f))
		}
	}

	fun spawn() {
		location.block.type = Material.END_GATEWAY
		val blockState = location.block.state as EndGateway
		blockState.age = Long.MAX_VALUE // This will effectively delay the beam effect for ages
		blockState.update(true)
		getPlayersWithinRange(location, 15U).forEach {
			it.playSound(Sound.sound(Key.key("minecraft", "entity.enderman.teleport"),
					Sound.Source.AMBIENT, 1.0f, 1.0f))
		}
	}
}
