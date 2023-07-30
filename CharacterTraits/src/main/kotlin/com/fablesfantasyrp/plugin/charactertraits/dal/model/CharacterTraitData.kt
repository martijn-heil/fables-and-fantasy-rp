package com.fablesfantasyrp.plugin.charactertraits.dal.model

import com.fablesfantasyrp.plugin.database.repository.Identifiable

data class CharacterTraitData(override val id: String, val description: String?) : Identifiable<String>
