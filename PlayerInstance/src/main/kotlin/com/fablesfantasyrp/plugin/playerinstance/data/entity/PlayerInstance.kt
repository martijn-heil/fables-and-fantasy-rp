package com.fablesfantasyrp.plugin.playerinstance.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.playerinstance.data.PlayerInstanceData
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

class PlayerInstance : DataEntity<Int, PlayerInstance>, PlayerInstanceData {
	override var dirtyMarker: DirtyMarker<PlayerInstance>? = null
	var isDestroyed = false

	override val id: Int

	private var ownerUUID: UUID
	override var owner: OfflinePlayer
		set(value) { if (ownerUUID != value.uniqueId) { ownerUUID = value.uniqueId; dirtyMarker?.markDirty(this) } }
		get() = Bukkit.getOfflinePlayer(ownerUUID)
	override var description: String? set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var isActive: Boolean set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	constructor(id: Int = -1,
				owner: OfflinePlayer,
				description: String?,
				isActive: Boolean,
				dirtyMarker: DirtyMarker<PlayerInstance>? = null) {
		this.id = id
		this.ownerUUID = owner.uniqueId
		this.owner = owner
		this.description = description
		this.isActive = isActive

		this.dirtyMarker = dirtyMarker
	}
}
