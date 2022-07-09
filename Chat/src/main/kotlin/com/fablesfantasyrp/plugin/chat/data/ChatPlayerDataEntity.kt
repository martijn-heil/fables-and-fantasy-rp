package com.fablesfantasyrp.plugin.chat.data

import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import java.time.Instant

class ChatPlayerDataEntity(override var isTyping: Boolean = false,
								override var lastTimeTyping: Instant? = null,
								override var lastTypingAnimation: String? = null,
								val persistent: PersistentChatPlayerData)
	: ChatPlayerData, PersistentChatPlayerData by persistent
