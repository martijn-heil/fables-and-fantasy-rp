package com.fablesfantasyrp.plugin.playerdata.database

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.playerdata.data.PlayerData
import net.kyori.adventure.text.format.Style
import org.bukkit.OfflinePlayer
import java.time.Instant

class DatabasePlayerData : PlayerData {
	private var dirtyMarker: DirtyMarker<PlayerData>? = null

	override val offlinePlayer: OfflinePlayer

	override var isTyping: Boolean = false
	override var lastTimeTyping: Instant = Instant.ofEpochSecond(0)
	override var lastTypingAnimation: String? = null

	override var currentCharacterId: ULong?
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var chatChannel: String
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var chatStyle: Style?
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var chatDisabledChannels: Set<String>
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	internal constructor(dirtyMarker: DirtyMarker<PlayerData>, player: OfflinePlayer, currentCharacter: ULong?,
						 chatChannel: String, chatStyle: Style?, chatDisabledChannels: Set<String>) {
		this.offlinePlayer = player
		this.currentCharacterId = currentCharacter
		this.chatChannel = chatChannel
		this.dirtyMarker = dirtyMarker
		this.chatStyle = chatStyle
		this.chatDisabledChannels = chatDisabledChannels
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
