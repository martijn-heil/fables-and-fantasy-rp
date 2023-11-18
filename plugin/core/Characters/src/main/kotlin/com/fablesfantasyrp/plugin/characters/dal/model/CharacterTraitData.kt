package com.fablesfantasyrp.plugin.characters.dal.model

import com.fablesfantasyrp.plugin.database.repository.Identifiable

data class CharacterTraitData(override val id: String, val displayName: String, val description: String?) : Identifiable<String>