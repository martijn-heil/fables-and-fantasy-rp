package com.fablesfantasyrp.plugin.chat.data.volatile

import java.time.Instant

interface VolatileChatPlayerData {
	var isTyping: Boolean
	var lastTimeTyping: Instant?
	var lastTypingAnimation: String?
}
