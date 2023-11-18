package com.fablesfantasyrp.plugin.profile.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import org.bukkit.OfflinePlayer

interface ProfileRepository :
		Repository<Profile>,
		MutableRepository<Profile>,
		KeyedRepository<Int, Profile> {
			fun allForOwner(offlinePlayer: OfflinePlayer?): Collection<Profile>
			fun activeForOwner(offlinePlayer: OfflinePlayer?): Collection<Profile> = this.allForOwner(offlinePlayer).filter { it.isActive }
}