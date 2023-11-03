package com.fablesfantasyrp.plugin.characters.web.model

import com.fablesfantasyrp.plugin.characters.dal.enums.Gender
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import kotlinx.serialization.Serializable

@Serializable
data class WebCharacter(val id: Int,
						val name: String,
						val race: Race,
						val age: UInt?,
						val gender: Gender,
						val description: String,
						val isDead: Boolean,
						val isShelved: Boolean,
						val lastSeen: Long?,
						val stats: WebCharacterStats,
						val totalStats: WebCharacterStats)
fun Character.transform() = WebCharacter(
	id = id,
	name = name,
	age = age,
	gender = gender,
	description = description,
	isDead = isDead,
	isShelved = isShelved,
	lastSeen = lastSeen?.epochSecond,
	race = race,
	stats = stats.transform(),
	totalStats = totalStats.transform()
)
