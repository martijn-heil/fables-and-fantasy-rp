package com.fablesfantasyrp.plugin.basicsystem.data.entity

import com.fablesfantasyrp.plugin.basicsystem.PLUGIN
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

class BasicSystemPlayer : DataEntity<UUID, BasicSystemPlayer> {
	override var dirtyMarker: DirtyMarker<BasicSystemPlayer>? = null
	override val id: UUID

	val offlinePlayer: OfflinePlayer get() = PLUGIN.server.getOfflinePlayer(id)
	val onlinePlayer: Player? get() = offlinePlayer.player

	var isEpic: Boolean set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	constructor(id: UUID, isEpic: Boolean, dirtyMarker: DirtyMarker<BasicSystemPlayer>? = null) {
		this.id = id
		this.isEpic = isEpic

		this.dirtyMarker = dirtyMarker // This must be last
	}

	fun doEpicJump() {
		if (isEpic) {
			onlinePlayer?.sendMessage("EPIC JUMP")
		}
	}
}
