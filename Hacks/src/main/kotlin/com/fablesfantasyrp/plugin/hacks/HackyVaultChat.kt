package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.ChatColor

class HackyVaultChat(child: Chat, perms: Permission) : DelegatedVaultChat(child, perms) {
	override fun getName(): String {
		return "HackyVaultChat(${super.getName()})"
	}

	@Deprecated("Deprecated in Java")
	override fun getPlayerPrefix(world: String?, player: String?): String {
		return when {
			// I could not live with that bloody donator star in front of my name..
			player == "Ninjoh" -> ChatColor.DARK_PURPLE.toString()
			else -> super.getPlayerPrefix(world, player)
		}
	}
}
