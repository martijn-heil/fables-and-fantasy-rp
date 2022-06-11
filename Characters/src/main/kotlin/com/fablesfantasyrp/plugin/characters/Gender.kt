package com.fablesfantasyrp.plugin.characters

enum class Gender(private val displayName: String) {
	FEMALE("female"),
	MALE("male"),
	OTHER("other");

	override fun toString() = displayName
}
