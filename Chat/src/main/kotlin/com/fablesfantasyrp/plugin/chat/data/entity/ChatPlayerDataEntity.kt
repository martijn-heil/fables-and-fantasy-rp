package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.data.ChatPlayerData
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import net.kyori.adventure.text.format.Style
import java.time.Instant
import java.util.*

class ChatPlayerDataEntity : ChatPlayerData, HasDirtyMarker<ChatPlayerData> {

	override var channel: ChatChannel
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var chatStyle: Style?
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override var disabledChannels: Set<ChatChannel>
		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	override val id: UUID

	constructor(channel: ChatChannel, chatStyle: Style?, disabledChannels: Set<ChatChannel>, id: UUID) {
		this.channel = channel
		this.chatStyle = chatStyle
		this.disabledChannels = disabledChannels
		this.id = id
	}

	override var dirtyMarker: DirtyMarker<ChatPlayerData>? = null

	override var isTyping: Boolean = false
	override var lastTimeTyping: Instant? = null
	override var lastTypingAnimation: String? = null

	override fun equals(other: Any?): Boolean = other is ChatPlayerData && other.id == this.id
	override fun hashCode(): Int = this.id.hashCode()
}
