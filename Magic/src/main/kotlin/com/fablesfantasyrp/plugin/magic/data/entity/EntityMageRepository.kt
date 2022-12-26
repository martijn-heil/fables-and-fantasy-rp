package com.fablesfantasyrp.plugin.magic.data.entity

import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.magic.MagicPath
import org.bukkit.plugin.Plugin

class EntityMageRepository<C>(private val plugin: Plugin, child: C) : MassivelyCachingEntityRepository<Long, Mage, C>(child), MageRepository
	where C: KeyedRepository<Long, Mage>,
		  C: MutableRepository<Mage>,
		  C: HasDirtyMarker<Mage> {

	fun forIdOrCreate(id: Long): Mage {
		val maybe = this.forId(id)
		return if (maybe != null) {
			maybe
		} else {
			val obj = Mage(
					id = id,
					magicPath = MagicPath.AEROMANCY,
					magicLevel = 0,
					spells = emptyList()
			)
			val result = this.create(obj)
			result.dirtyMarker = this
			result
		}
	}

	override fun forPlayerCharacter(c: PlayerCharacterData): Mage? {
		return this.forId(c.id.toLong())
	}

	override fun forPlayerCharacterOrCreate(c: CharacterData): Mage {
		return this.forIdOrCreate(c.id.toLong())
	}

	override fun destroy(v: Mage) {
		super.destroy(v)
		v.isDeleted = true
	}
}
