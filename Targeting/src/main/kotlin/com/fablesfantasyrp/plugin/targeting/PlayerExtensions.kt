package com.fablesfantasyrp.plugin.targeting

import com.fablesfantasyrp.plugin.targeting.data.SimpleTargetingPlayerData
import org.bukkit.OfflinePlayer

var OfflinePlayer.targeting: SimpleTargetingPlayerData
	get() = targetingPlayerDataRepository.forOfflinePlayer(this)
	set(value) = targetingPlayerDataRepository.update(value)
