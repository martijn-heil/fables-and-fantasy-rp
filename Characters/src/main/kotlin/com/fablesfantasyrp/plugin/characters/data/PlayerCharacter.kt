package com.fablesfantasyrp.plugin.characters.data

import com.fablesfantasyrp.plugin.characters.playerCharacterRepository
import org.bukkit.Location
import org.bukkit.OfflinePlayer

interface PlayerCharacter {
	val id: ULong
	var name: String
	var age: UInt
	var description: String
	val gender: Gender
	val race: Race
	val stats: CharacterStats
	val location: Location
	val money: ULong
	val player: OfflinePlayer

	companion object : PlayerCharacterRepository by playerCharacterRepository
}
