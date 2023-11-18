package com.fablesfantasyrp.plugin.lodestones.dal.repository

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.lodestones.dal.model.LodestoneBannerData

interface LodestoneBannerDataRepository : MutableRepository<LodestoneBannerData>, KeyedRepository<Int, LodestoneBannerData> {

}
