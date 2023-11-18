package com.fablesfantasyrp.plugin.bell.dal.repository

import com.fablesfantasyrp.plugin.bell.dal.model.BellData
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.NamedRepository

interface BellDataRepository : MutableRepository<BellData>, KeyedRepository<Int, BellData>, NamedRepository<BellData> {

}
