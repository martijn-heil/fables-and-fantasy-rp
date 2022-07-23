package com.fablesfantasyrp.plugin.knockout

import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerEntity
import org.bukkit.OfflinePlayer

val OfflinePlayer.knockout: KnockoutPlayerEntity
	get() = knockoutPlayerDataManager.forId(uniqueId)!!
