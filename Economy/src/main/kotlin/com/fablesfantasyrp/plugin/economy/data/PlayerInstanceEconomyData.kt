package com.fablesfantasyrp.plugin.economy.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable

interface PlayerInstanceEconomyData : Identifiable<Int> {
	var money: Int
	var bankMoney: Int
}
