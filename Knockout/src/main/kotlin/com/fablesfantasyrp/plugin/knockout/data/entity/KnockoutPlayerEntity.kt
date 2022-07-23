package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.knockout.data.KnockoutPlayerData
import org.bukkit.entity.Player

interface KnockoutPlayerEntity : KnockoutPlayerData {
	fun knockout(by: Player?)
	fun revive(by: Player?)
}
