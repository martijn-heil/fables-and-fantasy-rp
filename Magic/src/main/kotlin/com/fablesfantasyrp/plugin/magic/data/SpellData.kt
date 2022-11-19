package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.magic.MagicPath

interface SpellData : Identifiable<String> {
	val displayName: String
	val description: String
	val magicPath: MagicPath
	val level: Int
	val castingValue: Int
}
