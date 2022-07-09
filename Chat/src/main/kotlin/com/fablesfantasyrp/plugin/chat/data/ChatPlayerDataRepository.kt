package com.fablesfantasyrp.plugin.chat.data

import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import java.util.*

interface ChatPlayerDataRepository: EntityRepository<UUID, ChatPlayerData> {
}
