package com.fablesfantasyrp.plugin.shops.dal.repository

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.shops.dal.model.ShopData

interface ShopDataRepository : MutableRepository<ShopData>, KeyedRepository<Int, ShopData> {

}
