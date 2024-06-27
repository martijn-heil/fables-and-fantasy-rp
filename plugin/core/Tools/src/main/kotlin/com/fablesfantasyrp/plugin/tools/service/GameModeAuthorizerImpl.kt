/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.tools.service

import com.fablesfantasyrp.plugin.domain.BACKROOMS
import com.fablesfantasyrp.plugin.domain.EDEN
import com.fablesfantasyrp.plugin.domain.FLATROOM
import com.fablesfantasyrp.plugin.domain.PLOTS
import com.fablesfantasyrp.plugin.domain.service.GameModeAuthorizer
import com.fablesfantasyrp.plugin.tools.Permission
import com.fablesfantasyrp.plugin.utils.Services
import org.bukkit.GameMode
import org.bukkit.GameMode.CREATIVE
import org.bukkit.GameMode.SURVIVAL
import org.bukkit.World
import org.bukkit.entity.Player
import net.milkbowl.vault.permission.Permission as VaultPermission

class GameModeAuthorizerImpl : GameModeAuthorizer {
	private val vaultPermission get() = Services.get<VaultPermission>()

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
