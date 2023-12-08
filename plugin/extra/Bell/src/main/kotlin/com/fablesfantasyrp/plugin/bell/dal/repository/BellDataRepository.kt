package com.fablesfantasyrp.plugin.bell.dal.repository

import com.fablesfantasyrp.plugin.bell.dal.model.BellData
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.NamedRepository

interface BellDataRepository : MutableRepository<BellData>, KeyedRepository<Int, BellData>, NamedRepository<BellData> {

}
