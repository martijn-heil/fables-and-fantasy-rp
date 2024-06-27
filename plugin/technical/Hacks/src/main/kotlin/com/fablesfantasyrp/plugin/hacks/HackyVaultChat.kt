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
package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class HackyVaultChat(child: Chat, perms: Permission) : DelegatedVaultChat(child, perms) {
	private val NINJOH_CHAT_PREFIX: String = ChatColor.DARK_RED.toString()

	override fun getName(): String {
		return "HackyVaultChat(${super.getName()})"
	}

	@Deprecated("Deprecated in Java")
	override fun getPlayerPrefix(world: String?, player: String?): String {
		return when {
			// I could not live with that bloody donator star in front of my name..
			player == NINJOH_NAME -> NINJOH_CHAT_PREFIX
			else -> super.getPlayerPrefix(world, player)
		}
	}

	override fun getPlayerPrefix(world: String?, player: OfflinePlayer?): String {
		return when {
			player?.uniqueId == NINJOH_UUID -> NINJOH_CHAT_PREFIX
			else -> super.getPlayerPrefix(world, player)
		}
	}

	override fun getPlayerPrefix(player: Player?): String {
		return when {
			player?.uniqueId == NINJOH_UUID -> NINJOH_CHAT_PREFIX
			else -> super.getPlayerPrefix(player)
		}
	}
}
