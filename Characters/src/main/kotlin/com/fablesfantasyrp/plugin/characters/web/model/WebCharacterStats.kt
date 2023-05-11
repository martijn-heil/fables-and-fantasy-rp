package com.fablesfantasyrp.plugin.characters.web.model

import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import kotlinx.serialization.Serializable

@Serializable
data class WebCharacterStats(val strength: UInt = 0U,
									 val defense: UInt = 0U,
									 val agility: UInt = 0U,
									 val intelligence: UInt = 0U)
fun CharacterStats.transform() = WebCharacterStats(
	strength = strength,
	defense = defense,
	agility = agility,
	intelligence = intelligence
)
