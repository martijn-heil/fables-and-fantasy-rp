package com.fablesfantasyrp.plugin.chat.data

import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.volatile.VolatileChatPlayerData

interface ChatPlayerData : PersistentChatPlayerData, VolatileChatPlayerData
