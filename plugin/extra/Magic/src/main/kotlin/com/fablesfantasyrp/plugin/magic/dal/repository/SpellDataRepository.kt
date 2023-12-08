package com.fablesfantasyrp.plugin.magic.dal.repository

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData

interface SpellDataRepository :
		KeyedRepository<String, SpellData>,
	MutableRepository<SpellData> {
	fun forLevelAndPath(level: Int, path: MagicPath): Collection<SpellData>
}
