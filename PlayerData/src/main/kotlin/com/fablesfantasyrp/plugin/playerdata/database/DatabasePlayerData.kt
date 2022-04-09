package com.fablesfantasyrp.plugin.playerdata.database

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.playerdata.PlayerData
import org.bukkit.OfflinePlayer

class DatabasePlayerData : PlayerData {
	private var dirtyMarker: DirtyMarker<DatabasePlayerData>? = null

	override val offlinePlayer: OfflinePlayer

	override var currentCharacterId: ULong?
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var chatChannel: String
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	internal constructor(dirtyMarker: DirtyMarker<DatabasePlayerData>, player: OfflinePlayer, currentCharacter: ULong?, chatChannel: String) {
		this.offlinePlayer = player
		this.currentCharacterId = currentCharacter
		this.chatChannel = chatChannel
		this.dirtyMarker = dirtyMarker
	}

	override fun equals(other: Any?): Boolean {
		return if (other is DatabasePlayerData) {
			other.offlinePlayer.uniqueId == offlinePlayer.uniqueId
		} else false
	}

	override fun hashCode(): Int {
		return offlinePlayer.uniqueId.hashCode()
	}

	companion object
}
