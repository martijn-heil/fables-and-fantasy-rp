package com.fablesfantasyrp.plugin.lodestones.dal.repository

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.NamedRepository
import com.fablesfantasyrp.plugin.lodestones.dal.model.LodestoneData

interface LodestoneDataRepository : MutableRepository<LodestoneData>, KeyedRepository<Int, LodestoneData>, NamedRepository<LodestoneData> {

}
