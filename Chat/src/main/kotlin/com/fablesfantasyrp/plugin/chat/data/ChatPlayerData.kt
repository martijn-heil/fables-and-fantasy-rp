package com.fablesfantasyrp.plugin.chat.data

import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import java.time.Instant

interface ChatPlayerData : PersistentChatPlayerData {
	var isTyping: Boolean
	var lastTimeTyping: Instant?
	var lastTypingAnimation: String?
}
