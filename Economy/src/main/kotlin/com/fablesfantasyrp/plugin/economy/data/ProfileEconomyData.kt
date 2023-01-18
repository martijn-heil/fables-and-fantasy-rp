package com.fablesfantasyrp.plugin.economy.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable

interface ProfileEconomyData : Identifiable<Int> {
	var money: Int
	var bankMoney: Int
}
