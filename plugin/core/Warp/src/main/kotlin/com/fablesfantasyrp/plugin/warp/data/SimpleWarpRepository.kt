package com.fablesfantasyrp.plugin.warp.data

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface SimpleWarpRepository : MutableRepository<SimpleWarp>, KeyedRepository<String, SimpleWarp> {
	fun isSyntacticallyValidId(id: String): Boolean
}
