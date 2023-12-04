package com.fablesfantasyrp.plugin.characters.dal.model

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.dal.enums.Gender
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.CharacterStats
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import java.time.Instant

data class CharacterData(
	override val id: Int,
	val name: String,
	val description: String,
	val gender: Gender,
	val race: Race,
	val stats: CharacterStats,
	val lastSeen: Instant? = null,
	val createdAt: Instant? = Instant.now(),
	val dateOfBirth: FablesLocalDate?,
	val dateOfNaturalDeath: FablesLocalDate?,
	val isDead: Boolean = false,
	val diedAt: Instant? = null,
	val isShelved: Boolean = false,
	val shelvedAt: Instant? = null,
	val changedStatsAt: Instant? = null,
	val traits: Set<CharacterTrait>) : Identifiable<Int> {

	val maximumHealth: UInt get() = (12 + CharacterStatKind.STRENGTH.getRollModifierFor(stats.strength)).toUInt()
}
