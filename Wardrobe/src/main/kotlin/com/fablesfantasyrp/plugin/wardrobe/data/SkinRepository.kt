package com.fablesfantasyrp.plugin.wardrobe.data

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface SkinRepository : MutableRepository<Skin>, KeyedRepository<Int, Skin> {
	fun forValue(value: String): Skin
}
