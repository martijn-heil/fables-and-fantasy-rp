package com.fablesfantasyrp.plugin.chat.data.volatile

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import java.time.Instant

interface VolatileChatPlayerData {
	var isTyping: Boolean
	var lastTimeTyping: Instant?
	var lastTypingAnimation: String?
	var previewChannel: ChatChannel?
}
