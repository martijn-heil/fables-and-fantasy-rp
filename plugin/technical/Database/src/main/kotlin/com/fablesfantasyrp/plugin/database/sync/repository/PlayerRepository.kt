package com.fablesfantasyrp.plugin.database.sync.repository

import com.fablesfantasyrp.plugin.database.model.Identifiable
import org.bukkit.OfflinePlayer
import java.util.*

interface PlayerRepository<T: Identifiable<UUID>> : KeyedRepository<UUID, T> {
	fun forPlayer(player: OfflinePlayer): T
}
