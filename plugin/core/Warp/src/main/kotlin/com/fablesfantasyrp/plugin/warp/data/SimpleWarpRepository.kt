package com.fablesfantasyrp.plugin.warp.data

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository

interface SimpleWarpRepository : MutableRepository<SimpleWarp>, KeyedRepository<String, SimpleWarp> {
	fun isSyntacticallyValidId(id: String): Boolean
}
