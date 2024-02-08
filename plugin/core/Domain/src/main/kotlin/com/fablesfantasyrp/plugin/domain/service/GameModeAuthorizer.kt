package com.fablesfantasyrp.plugin.domain.service

import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.entity.Player

interface GameModeAuthorizer {
	fun mayAccess(player: Player, gameMode: GameMode, world: World): Boolean
	fun getDefaultGameMode(world: World): GameMode
	fun getPreferredGameMode(player: Player, world: World): GameMode
}
