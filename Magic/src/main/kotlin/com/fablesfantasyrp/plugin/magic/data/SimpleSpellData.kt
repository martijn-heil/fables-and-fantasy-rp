package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.magic.MagicPath

data class SimpleSpellData(override val id: String,
						   override val displayName: String,
						   override val description: String,
						   override val magicPath: MagicPath,
						   override val level: Int,
						   override val castingValue: Int) : SpellData
