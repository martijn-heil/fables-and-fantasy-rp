package com.fablesfantasyrp.plugin.characters.domain.mapper

import com.fablesfantasyrp.plugin.characters.dal.model.CharacterData
import com.fablesfantasyrp.plugin.characters.dal.repository.CharacterDataRepository
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.database.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import org.bukkit.OfflinePlayer

class CharacterMapper(private val child: CharacterDataRepository, private val profiles: ProfileRepository)
	: MappingRepository<Int, CharacterData, Character, CharacterDataRepository>(child), CharacterRepository, HasDirtyMarker<Character> {

	override var dirtyMarker: DirtyMarker<Character>? = null

	override fun convertFromChild(v: CharacterData) = Character(
		id = v.id,
		profile = profiles.forId(v.id)!!,
		name = v.name,
		description = v.description,
		stats = v.stats,
		race = v.race,
		gender = v.gender,
		age = v.age,
		lastSeen = v.lastSeen,
		createdAt = v.createdAt,
		isDead = v.isDead,
		diedAt = v.diedAt,
		isShelved = v.isShelved,
		shelvedAt = v.shelvedAt,
		changedStatsAt = v.changedStatsAt,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: Character) = CharacterData(
		id = v.id,
		name = v.name,
		description = v.description,
		stats = v.stats,
		race = v.race,
		gender = v.gender,
		age = v.age,
		lastSeen = v.lastSeen,
		createdAt = v.createdAt,
		isDead = v.isDead,
		diedAt = v.diedAt,
		isShelved = v.isShelved,
		shelvedAt = v.shelvedAt,
		changedStatsAt = v.changedStatsAt
	)

	override fun forOwner(offlinePlayer: OfflinePlayer?): Collection<Character> = child.forOwner(offlinePlayer).map { convertFromChild(it) }
	override fun forProfile(profile: Profile): Character? = child.forProfile(profile)?.let { convertFromChild(it) }
	override fun forName(name: String): Character? = child.forName(name)?.let { convertFromChild(it) }
	override fun nameExists(name: String): Boolean = child.nameExists(name)
	override val nameMap: Map<String, Int> = child.nameMap
}
