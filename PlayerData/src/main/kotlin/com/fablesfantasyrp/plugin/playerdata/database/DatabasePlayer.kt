package com.fablesfantasyrp.plugin.playerdata.database

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import org.bukkit.OfflinePlayer

class DatabasePlayer {
	private var dirtyMarker: DirtyMarker<DatabasePlayer>? = null

	val player: OfflinePlayer

	var currentCharacterId: ULong
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var chatChannel: String
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	internal constructor(dirtyMarker: DirtyMarker<DatabasePlayer>, player: OfflinePlayer, currentCharacter: ULong, chatChannel: String) {
		this.player = player
		this.currentCharacterId = currentCharacter
		this.chatChannel = chatChannel
		this.dirtyMarker = dirtyMarker
	}

	override fun equals(other: Any?): Boolean {
		return if (other is DatabasePlayer) {
			other.player.uniqueId == player.uniqueId
		} else false
	}

	override fun hashCode(): Int {
		return player.uniqueId.hashCode()
	}

	companion object
}
