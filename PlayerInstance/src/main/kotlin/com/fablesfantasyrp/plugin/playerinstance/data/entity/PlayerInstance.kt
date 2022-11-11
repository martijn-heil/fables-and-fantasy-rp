package com.fablesfantasyrp.plugin.playerinstance.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.playerinstance.data.PlayerInstanceData
import org.bukkit.OfflinePlayer

class PlayerInstance : DataEntity<Int, PlayerInstance>, PlayerInstanceData {
	override var dirtyMarker: DirtyMarker<PlayerInstance>? = null
	var isDestroyed = false

	override val id: Int
	override val owner: OfflinePlayer

	constructor(id: Int, owner: OfflinePlayer,
				dirtyMarker: DirtyMarker<PlayerInstance>? = null) {
		this.id = id
		this.owner = owner

		this.dirtyMarker = dirtyMarker
	}
}
