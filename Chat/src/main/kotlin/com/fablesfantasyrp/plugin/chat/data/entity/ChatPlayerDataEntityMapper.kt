package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.data.ChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerDataRepository
import com.fablesfantasyrp.plugin.database.entity.AbstractEntityMapper
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import java.util.*

class ChatPlayerDataEntityMapper(private val child: PersistentChatPlayerDataRepository)
	: AbstractEntityMapper<UUID, PersistentChatPlayerData, ChatPlayerData, PersistentChatPlayerDataRepository>(child),
		HasDirtyMarker<ChatPlayerData> {
	override var dirtyMarker: DirtyMarker<ChatPlayerData>? = null

	override fun forId(id: UUID): ChatPlayerData? = child.forId(id)?.let { convert(it) }
	override fun all(): Collection<ChatPlayerData> = child.all().map { convert(it) }

	private fun convert(it: PersistentChatPlayerData): ChatPlayerData
		= ChatPlayerDataEntity(it.channel, it.chatStyle, it.disabledChannels, it.id)
}
