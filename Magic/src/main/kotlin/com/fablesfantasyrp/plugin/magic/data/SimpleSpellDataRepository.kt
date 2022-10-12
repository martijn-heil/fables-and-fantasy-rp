package com.fablesfantasyrp.plugin.magic.data

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.magic.MagicPath

interface SimpleSpellDataRepository :
		KeyedRepository<String, SimpleSpellData>,
		MutableRepository<SimpleSpellData> {
	fun forLevelAndPath(level: Int, path: MagicPath): Collection<SimpleSpellData>
}
