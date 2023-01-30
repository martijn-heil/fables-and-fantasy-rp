package com.fablesfantasyrp.plugin.characters.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import org.bukkit.Location
import org.bukkit.OfflinePlayer

interface CharacterData : Identifiable<Int> {
	var name: String
	var age: UInt
	var description: String
	val gender: Gender
	val race: Race
	val stats: CharacterStats
	val location: Location
	val money: ULong
	val player: OfflinePlayer

	val maximumHealth: UInt get() = (12 + CharacterStatKind.STRENGTH.getRollModifierFor(stats.strength)).toUInt()
}
