package com.fablesfantasyrp.plugin.characters.command.provider

fun quote(s: String): String {
	return if (s.contains(" ")) "\"$s\"" else s
}
