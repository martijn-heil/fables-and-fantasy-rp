package com.fablesfantasyrp.plugin.discord

import org.bukkit.OfflinePlayer

interface DiscordLinkService {
	fun getOfflinePlayer(discordId: String): OfflinePlayer?
	fun getDiscordId(offlinePlayer: OfflinePlayer): String?
}
