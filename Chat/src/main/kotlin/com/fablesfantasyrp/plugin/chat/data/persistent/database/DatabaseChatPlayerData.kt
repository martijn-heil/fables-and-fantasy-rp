package com.fablesfantasyrp.plugin.chat.data.persistent.database

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.channel.ToggleableChatChannel
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import net.kyori.adventure.text.format.Style
import java.util.*

data class DatabaseChatPlayerData(override var channel: ChatChannel,
								  override var chatStyle: Style?,
								  override var disabledChannels: Set<ToggleableChatChannel>,
								  override val id: UUID) : PersistentChatPlayerData {

}
