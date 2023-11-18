package com.fablesfantasyrp.plugin.database.repository

import org.bukkit.OfflinePlayer
import java.util.*

interface PlayerRepository<T: Identifiable<UUID>> : KeyedRepository<UUID, T> {
	fun forPlayer(player: OfflinePlayer): T
}
