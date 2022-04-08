package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.characters.PlayerCharacter
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import org.bukkit.OfflinePlayer

class FablesPlayer {
	private var dirtyMarker: DirtyMarker<FablesPlayer>? = null

	val player: OfflinePlayer

	var currentCharacter: PlayerCharacter
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var chatChannel: String
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	internal constructor(dirtyMarker: DirtyMarker<FablesPlayer>, player: OfflinePlayer, currentCharacter: PlayerCharacter, chatChannel: String) {
		this.player = player
		this.currentCharacter = currentCharacter
		this.chatChannel = chatChannel
		this.dirtyMarker = dirtyMarker
	}

	override fun equals(other: Any?): Boolean {
		return if (other is FablesPlayer) {
			other.player.uniqueId == player.uniqueId
		} else false
	}

	override fun hashCode(): Int {
		return player.uniqueId.hashCode()
	}
}
