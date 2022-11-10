package com.fablesfantasyrp.plugin.fasttravel.data.entity

import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import org.bukkit.entity.Player
import java.util.*

interface FastTravelPlayerRepository : EntityRepository<UUID, FastTravelPlayer> {
	fun forPlayer(p: Player): FastTravelPlayer
}
