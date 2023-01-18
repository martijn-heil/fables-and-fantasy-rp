package com.fablesfantasyrp.plugin.profile.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.OfflinePlayer

class EntityProfileRepositoryImpl<C>(child: C) :
		MassivelyCachingEntityRepository<Int, Profile, C>(child), EntityProfileRepository
		where C: KeyedRepository<Int, Profile>,
			  C: MutableRepository<Profile>,
			  C: HasDirtyMarker<Profile>,
			  C: ProfileRepository {
	override fun forOwner(offlinePlayer: OfflinePlayer): Collection<Profile> {
		this.saveAllDirty()
		return child.forOwner(offlinePlayer).mapNotNull { this.forId(it.id) }
	}
}
