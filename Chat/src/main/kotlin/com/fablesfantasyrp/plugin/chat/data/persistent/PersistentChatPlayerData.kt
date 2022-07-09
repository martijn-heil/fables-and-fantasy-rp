package com.fablesfantasyrp.plugin.chat.data.persistent

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.database.repository.Identifiable
import net.kyori.adventure.text.format.Style
import java.util.*

interface PersistentChatPlayerData : Identifiable<UUID> {
	var channel: ChatChannel
	var chatStyle: Style?
	var disabledChannels: Set<ChatChannel>
}
