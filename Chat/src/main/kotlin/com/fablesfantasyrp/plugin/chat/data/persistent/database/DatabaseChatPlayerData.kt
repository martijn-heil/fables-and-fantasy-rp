package com.fablesfantasyrp.plugin.chat.data.persistent.database

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.channel.ChatOutOfCharacter
import com.fablesfantasyrp.plugin.chat.channel.ToggleableChatChannel
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import net.kyori.adventure.text.format.Style
import java.util.*

data class DatabaseChatPlayerData(override val id: UUID,
								  override var channel: ChatChannel = ChatOutOfCharacter,
								  override var chatStyle: Style? = null,
								  override var disabledChannels: Set<ToggleableChatChannel> = emptySet(),
								  override var isChatSpyEnabled: Boolean = false,
								  override var chatSpyExcludeChannels: Set<ChatChannel> = emptySet(),
								  override var isReceptionIndicatorEnabled: Boolean = false) : PersistentChatPlayerData
