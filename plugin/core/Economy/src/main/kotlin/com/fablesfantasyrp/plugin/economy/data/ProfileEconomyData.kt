package com.fablesfantasyrp.plugin.economy.data

import com.fablesfantasyrp.plugin.database.model.Identifiable

interface ProfileEconomyData : Identifiable<Int> {
	var money: Int
	var bankMoney: Int
}
