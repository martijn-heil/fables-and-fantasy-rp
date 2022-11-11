package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.OfflinePlayer

class EntityPlayerCharacterRepository<C>(child: C) : SimpleEntityRepository<ULong, PlayerCharacter, C>(child), PlayerCharacterRepository
		where C: KeyedRepository<ULong, PlayerCharacter>,
			  C: MutableRepository<PlayerCharacter>,
			  C: HasDirtyMarker<PlayerCharacter> {

	override fun forOwner(offlinePlayer: OfflinePlayer) {
		TODO("Not yet implemented")
	}
}
