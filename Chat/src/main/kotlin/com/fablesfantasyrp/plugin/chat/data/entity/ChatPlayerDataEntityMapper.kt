package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerDataRepository
import com.fablesfantasyrp.plugin.database.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import java.util.*

class ChatPlayerDataEntityMapper(private val child: PersistentChatPlayerDataRepository)
	: MappingRepository<UUID, PersistentChatPlayerData, ChatPlayerEntity, PersistentChatPlayerDataRepository>(child),
		HasDirtyMarker<ChatPlayerEntity> {
	override var dirtyMarker: DirtyMarker<ChatPlayerEntity>? = null

	override fun forId(id: UUID): ChatPlayerEntity? = child.forId(id)?.let { convertFromChild(it) }
	override fun all(): Collection<ChatPlayerEntity> = child.all().map { convertFromChild(it) }

	override fun convertToChild(v: ChatPlayerEntity): PersistentChatPlayerData = v
	override fun convertFromChild(v: PersistentChatPlayerData): ChatPlayerEntity {
		val obj = ChatPlayerDataEntity(v.id, v.channel, v.chatStyle, v.disabledChannels, v.isReceptionIndicatorEnabled)
		obj.dirtyMarker = dirtyMarker
		return obj
	}
}
