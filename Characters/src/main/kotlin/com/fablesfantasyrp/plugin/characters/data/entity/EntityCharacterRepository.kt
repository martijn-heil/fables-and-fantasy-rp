package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import org.bukkit.OfflinePlayer

class EntityCharacterRepository<C>(child: C, private val playerInstances: PlayerInstanceRepository) : SimpleEntityRepository<ULong, Character, C>(child), CharacterRepository
		where C: KeyedRepository<ULong, Character>,
			  C: MutableRepository<Character>,
			  C: HasDirtyMarker<Character> {

	override fun forOwner(offlinePlayer: OfflinePlayer): Collection<Character> {
		return playerInstances.forOwner(offlinePlayer).mapNotNull { this.forId(it.id.toULong()) }
	}
}
