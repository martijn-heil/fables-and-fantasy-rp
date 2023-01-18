package com.fablesfantasyrp.plugin.economy.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.economy.data.ProfileEconomyData

class ProfileEconomy : DataEntity<Int, ProfileEconomy>, ProfileEconomyData {
	var isDestroyed = false
	override val id: Int
	override var dirtyMarker: DirtyMarker<ProfileEconomy>? = null

	override var money: Int set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var bankMoney: Int set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	constructor(id: Int, money: Int, bankMoney: Int, dirtyMarker: DirtyMarker<ProfileEconomy>? = null) {
		this.id = id
		this.money = money
		this.bankMoney = bankMoney
		this.dirtyMarker = dirtyMarker
	}
}
