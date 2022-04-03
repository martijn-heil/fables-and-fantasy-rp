package com.fablesfantasyrp.plugin.characters

import org.bukkit.Location
import org.bukkit.OfflinePlayer

interface PlayerCharacter {
	val id: UInt
	var name: String
	var age: UInt
	var description: String
	val gender: Gender
	val race: Race
	val stats: CharacterStats
	val location: Location
	val money: Long
	val player: OfflinePlayer
}
