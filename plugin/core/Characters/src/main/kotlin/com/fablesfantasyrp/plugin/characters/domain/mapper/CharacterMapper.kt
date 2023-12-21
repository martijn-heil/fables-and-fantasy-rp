package com.fablesfantasyrp.plugin.characters.domain.mapper

import com.fablesfantasyrp.plugin.characters.dal.model.CharacterData
import com.fablesfantasyrp.plugin.characters.dal.repository.CharacterDataRepository
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.database.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import org.bukkit.OfflinePlayer

class CharacterMapper(private val child: CharacterDataRepository, private val profiles: ProfileRepository)
	: MappingRepository<Int, CharacterData, Character, CharacterDataRepository>(child), HasDirtyMarker<Character> {

	override var dirtyMarker: DirtyMarker<Character>? = null

	override fun convertFromChild(v: CharacterData) = Character(
		id = v.id,
		profile = profiles.forId(v.id)!!,
		name = v.name,
		description = v.description,
		stats = v.stats,
		race = v.race,
		gender = v.gender,
		dateOfBirth = v.dateOfBirth,
		dateOfNaturalDeath = v.dateOfNaturalDeath,
		lastSeen = v.lastSeen,
		createdAt = v.createdAt,
		isDead = v.isDead,
		diedAt = v.diedAt,
		isShelved = v.isShelved,
		shelvedAt = v.shelvedAt,
		changedStatsAt = v.changedStatsAt,
		traits = v.traits,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: Character) = CharacterData(
		id = v.id,
		name = v.name,
		description = v.description,
		stats = v.stats,
		race = v.race,
		gender = v.gender,
		dateOfBirth = v.dateOfBirth,
		dateOfNaturalDeath = v.dateOfNaturalDeath,
		lastSeen = v.lastSeen,
		createdAt = v.createdAt,
		isDead = v.isDead,
		diedAt = v.diedAt,
		isShelved = v.isShelved,
		shelvedAt = v.shelvedAt,
		changedStatsAt = v.changedStatsAt,
		traits = v.traits
	)

	fun forOwner(offlinePlayer: OfflinePlayer?): Collection<Character> = child.forOwner(offlinePlayer).map { convertFromChild(it) }
	fun forProfile(profile: Profile): Character? = child.forProfile(profile)?.let { convertFromChild(it) }
	fun forName(name: String): Character? = child.forName(name)?.let { convertFromChild(it) }
	fun nameExists(name: String): Boolean = child.nameExists(name)
	val nameMap: Map<String, Int> = child.nameMap
}
