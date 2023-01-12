package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.entity.Player

interface PlayerInstanceSelectionPrompter {
	suspend fun promptSelect(player: Player, playerInstances: Collection<PlayerInstance>): PlayerInstance
}
