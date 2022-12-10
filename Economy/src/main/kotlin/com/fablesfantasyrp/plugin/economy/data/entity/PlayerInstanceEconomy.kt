package com.fablesfantasyrp.plugin.economy.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.economy.data.PlayerInstanceEconomyData

class PlayerInstanceEconomy : DataEntity<Int, PlayerInstanceEconomy>, PlayerInstanceEconomyData {
	var isDestroyed = false
	override val id: Int
	override var dirtyMarker: DirtyMarker<PlayerInstanceEconomy>? = null

	override var money: Int set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	override var bankMoney: Int set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	constructor(id: Int, money: Int, bankMoney: Int, dirtyMarker: DirtyMarker<PlayerInstanceEconomy>? = null) {
		this.id = id
		this.money = money
		this.bankMoney = bankMoney
		this.dirtyMarker = dirtyMarker
	}
}
