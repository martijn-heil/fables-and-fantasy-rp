package com.fablesfantasyrp.plugin.fasttravel.data.entity

import com.fablesfantasyrp.plugin.database.entity.SimpleMapEntityRepository
import org.bukkit.entity.Player
import java.util.*

class MapFastTravelPlayerRepository : SimpleMapEntityRepository<UUID, FastTravelPlayer>(), FastTravelPlayerRepository {
	override fun forPlayer(p: Player)
		= this.forId(p.uniqueId) ?: this.create(FastTravelPlayer(p.uniqueId, this))
}
