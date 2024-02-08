package com.fablesfantasyrp.plugin.tools.service

import com.fablesfantasyrp.plugin.domain.BACKROOMS
import com.fablesfantasyrp.plugin.domain.EDEN
import com.fablesfantasyrp.plugin.domain.FLATROOM
import com.fablesfantasyrp.plugin.domain.PLOTS
import com.fablesfantasyrp.plugin.domain.service.GameModeAuthorizer
import com.fablesfantasyrp.plugin.tools.Permission
import org.bukkit.GameMode
import org.bukkit.GameMode.CREATIVE
import org.bukkit.GameMode.SURVIVAL
import org.bukkit.World
import org.bukkit.entity.Player
import net.milkbowl.vault.permission.Permission as VaultPermission

class GameModeAuthorizerImpl(private val vaultPermission: VaultPermission) : GameModeAuthorizer {
	override fun mayAccess(player: Player, gameMode: GameMode, world: World): Boolean
		= getDefaultGameMode(world) == gameMode || vaultPermission.playerHas(world.name, player, getPermission(gameMode))

	override fun getDefaultGameMode(world: World): GameMode = when (world) {
		PLOTS -> CREATIVE
		FLATROOM -> CREATIVE
		EDEN -> SURVIVAL
		BACKROOMS -> SURVIVAL
		else -> SURVIVAL
	}

	override fun getPreferredGameMode(player: Player, world: World): GameMode = when (world) {
		PLOTS -> CREATIVE
		FLATROOM -> CREATIVE
		EDEN -> SURVIVAL
		BACKROOMS -> if (mayAccess(player, CREATIVE, world)) CREATIVE else SURVIVAL
		else -> SURVIVAL
	}

	private fun getPermission(gameMode: GameMode) = "${Permission.Command.GameMode}.${gameMode.name.lowercase()}"
}
