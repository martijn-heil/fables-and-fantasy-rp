package com.fablesfantasyrp.plugin.characters.domain.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker

class CharacterTrait : DataEntity<String, CharacterTrait> {
	override var dirtyMarker: DirtyMarker<CharacterTrait>?
	override val id: String
	var displayName: String		set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	var description: String?	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	constructor(id: String, displayName: String, description: String?, dirtyMarker: DirtyMarker<CharacterTrait>? = null) {
		this.id = id
		this.displayName = displayName
		this.description = description

		this.dirtyMarker = dirtyMarker
	}
}
