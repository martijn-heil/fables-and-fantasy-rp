package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import org.bukkit.OfflinePlayer

class EntityCharacterRepositoryImpl<C>(child: C, private val playerInstances: PlayerInstanceRepository)
	: SimpleEntityRepository<ULong, Character, C>(child), EntityCharacterRepository
		where C: HasDirtyMarker<Character>,
              C: CharacterRepository {

	override lateinit var nameMap: HashMap<String, ULong>

	override fun init() {
		super.init()
		nameMap = HashMap(child.nameMap)
	}

	override fun create(v: Character): Character {
		val result = super.create(v)
		nameMap[result.name] = result.id
		return result
	}

	override fun destroy(v: Character) {
		super.destroy(v)
		nameMap.remove(v.name)
	}

	override fun forOwner(offlinePlayer: OfflinePlayer): Collection<Character> {
		return playerInstances.forOwner(offlinePlayer).mapNotNull { this.forId(it.id.toULong()) }
	}

	override fun forPlayerInstance(playerInstance: PlayerInstance): Character? {
		return this.forId(playerInstance.id.toULong())
	}

	override fun forName(name: String): Character? {
		return nameMap[name]?.let { this.forId(it) }
	}
}
