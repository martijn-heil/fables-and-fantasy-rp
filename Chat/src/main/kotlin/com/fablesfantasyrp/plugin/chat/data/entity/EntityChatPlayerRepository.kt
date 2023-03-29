package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.data.ChatPlayerRepository
import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import java.util.*

interface EntityChatPlayerRepository : EntityRepository<UUID, ChatPlayer>, ChatPlayerRepository
