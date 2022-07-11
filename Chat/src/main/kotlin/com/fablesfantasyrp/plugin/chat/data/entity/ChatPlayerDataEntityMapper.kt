package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerDataRepository
import com.fablesfantasyrp.plugin.database.entity.AbstractEntityMapper
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import java.util.*

class ChatPlayerDataEntityMapper(private val child: PersistentChatPlayerDataRepository)
	: AbstractEntityMapper<UUID, PersistentChatPlayerData, ChatPlayerEntity, PersistentChatPlayerDataRepository>(child),
		HasDirtyMarker<ChatPlayerEntity> {
	override var dirtyMarker: DirtyMarker<ChatPlayerEntity>? = null

	override fun forId(id: UUID): ChatPlayerEntity? = child.forId(id)?.let { convert(it) }
	override fun all(): Collection<ChatPlayerEntity> = child.all().map { convert(it) }

	private fun convert(it: PersistentChatPlayerData): ChatPlayerEntity {
		val obj = ChatPlayerDataEntity(it.id, it.channel, it.chatStyle, it.disabledChannels, it.isReceptionIndicatorEnabled)
		obj.dirtyMarker = dirtyMarker
		return obj
	}
}
