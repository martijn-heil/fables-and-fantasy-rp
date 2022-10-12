package com.fablesfantasyrp.plugin.characters.data.simple

import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Gender
import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.characters.data.Race
import org.bukkit.Location
import org.bukkit.OfflinePlayer

data class SimplePlayerCharacterData(override val id: ULong,
									 override var name: String,
									 override var age: UInt,
									 override var description: String,
									 override val gender: Gender,
									 override val race: Race,
									 override val stats: CharacterStats,
									 override val location: Location,
									 override val money: ULong,
									 override val player: OfflinePlayer): PlayerCharacterData {

}
