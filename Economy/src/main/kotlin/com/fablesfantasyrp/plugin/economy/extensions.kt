package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

var PlayerInstance.money: Int
	get() = PLUGIN.playerInstanceEconomyRepository.forPlayerInstance(this).money
	set(value) { PLUGIN.playerInstanceEconomyRepository.forPlayerInstance(this).money = value }
