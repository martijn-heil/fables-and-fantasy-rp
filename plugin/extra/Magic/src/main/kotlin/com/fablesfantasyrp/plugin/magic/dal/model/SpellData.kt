package com.fablesfantasyrp.plugin.magic.dal.model

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath

data class SpellData(override val id: String,
					 val displayName: String,
					 val description: String,
					 val magicPath: MagicPath,
					 val level: Int,
					 val castingValue: Int) : Identifiable<String> {
	override fun equals(other: Any?): Boolean {
		return other is SpellData && other.id == this.id
	}

	override fun hashCode(): Int {
		return this.id.hashCode()
	}
}
