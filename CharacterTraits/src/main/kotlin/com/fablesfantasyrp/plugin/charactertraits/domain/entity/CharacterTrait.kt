package com.fablesfantasyrp.plugin.charactertraits.domain.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker

class CharacterTrait : DataEntity<String, CharacterTrait> {
	override var dirtyMarker: DirtyMarker<CharacterTrait>?
	override val id: String

	var description: String?	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	constructor(id: String, description: String?, dirtyMarker: DirtyMarker<CharacterTrait>? = null) {
		this.id = id
		this.dirtyMarker = dirtyMarker
		this.description = description
	}
}
