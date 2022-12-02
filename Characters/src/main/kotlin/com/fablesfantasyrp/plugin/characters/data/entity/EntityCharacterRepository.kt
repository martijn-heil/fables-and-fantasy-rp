package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.database.entity.EntityRepository

interface EntityCharacterRepository : EntityRepository<ULong, Character>, CharacterRepository {
}
