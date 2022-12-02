package com.fablesfantasyrp.plugin.economy.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable

interface PlayerInstanceEconomyData : Identifiable<Int> {
	val money: Int
}
