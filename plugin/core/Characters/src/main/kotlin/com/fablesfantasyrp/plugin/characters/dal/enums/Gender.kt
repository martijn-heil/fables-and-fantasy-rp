package com.fablesfantasyrp.plugin.characters.dal.enums

enum class Gender(private val displayName: String) {
	FEMALE("female"),
	MALE("male"),
	OTHER("other");

	override fun toString() = displayName
}
