package com.fablesfantasyrp.plugin.lodestones.dal.repository

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.lodestones.dal.model.LodestoneBannerData

interface LodestoneBannerDataRepository : MutableRepository<LodestoneBannerData>, KeyedRepository<Int, LodestoneBannerData> {

}
