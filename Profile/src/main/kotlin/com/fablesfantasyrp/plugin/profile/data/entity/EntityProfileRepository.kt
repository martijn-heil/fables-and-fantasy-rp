package com.fablesfantasyrp.plugin.profile.data.entity

import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import org.bukkit.OfflinePlayer

interface EntityProfileRepository : EntityRepository<Int, Profile>, ProfileRepository {
	override fun forOwner(offlinePlayer: OfflinePlayer): Collection<Profile>
}
