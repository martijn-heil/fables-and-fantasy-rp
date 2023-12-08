package com.fablesfantasyrp.plugin.lodestones.dal.repository

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.NamedRepository
import com.fablesfantasyrp.plugin.lodestones.dal.model.LodestoneData

interface LodestoneDataRepository : MutableRepository<LodestoneData>, KeyedRepository<Int, LodestoneData>, NamedRepository<LodestoneData> {

}
