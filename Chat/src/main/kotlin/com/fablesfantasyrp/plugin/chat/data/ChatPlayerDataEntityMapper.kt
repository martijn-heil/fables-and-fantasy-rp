package com.fablesfantasyrp.plugin.chat.data

import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerDataRepository
import com.fablesfantasyrp.plugin.database.entity.AbstractEntityMapper
import java.util.*

class ChatPlayerDataEntityMapper(private val child: PersistentChatPlayerDataRepository)
	: AbstractEntityMapper<UUID, PersistentChatPlayerData, ChatPlayerData, PersistentChatPlayerDataRepository>(child) {
	override fun forId(id: UUID): ChatPlayerData? = child.forId(id)?.let { ChatPlayerDataEntity(persistent = it) }
	override fun allIds(): Collection<UUID> = child.allIds()
	override fun all(): Collection<ChatPlayerData> = child.all().map { ChatPlayerDataEntity(persistent = it) }
	override fun destroy(v: ChatPlayerData) = child.destroy(v)
	override fun create(v: ChatPlayerData) = child.create(v)
	override fun update(v: ChatPlayerData) = child.update(v)
}
