package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

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
