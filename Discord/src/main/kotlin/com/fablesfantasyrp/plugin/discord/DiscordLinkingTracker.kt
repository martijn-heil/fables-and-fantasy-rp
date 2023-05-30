package com.fablesfantasyrp.plugin.discord

import com.denizenscript.denizen.objects.PlayerTag
import com.fablesfantasyrp.plugin.denizeninterop.denizenParseTag
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import java.util.*

class DiscordLinkingTracker(private val plugin: Plugin) : DiscordLinkService {
	private val server = plugin.server
	private val logger = plugin.logger

	private val byDiscord = HashMap<String, UUID>()
	private val byPlayer = HashMap<UUID, String>()

	fun start() {
		buildInitialCache()
		logger.info("${byPlayer.size} players have linked their discord accounts.")
		server.pluginManager.registerEvents(DiscordLinkingTrackerListener(), plugin)
	}

	override fun getOfflinePlayer(discordId: String): OfflinePlayer?
		= byDiscord[discordId]?.let { server.getOfflinePlayer(it) }

	override fun getDiscordId(offlinePlayer: OfflinePlayer): String?
		= byPlayer[offlinePlayer.uniqueId]

	private fun set(player: OfflinePlayer, discordId: String?) {
		val oldDiscordId = byPlayer.remove(player.uniqueId)
		if (oldDiscordId != null) byDiscord.remove(oldDiscordId)

		if (discordId != null) {
			byDiscord[discordId] = player.uniqueId
			byPlayer[player.uniqueId] = discordId
		}
	}

	private fun buildInitialCache() {
		server.offlinePlayers.forEach {
			val discordId = denizenGetDiscordId(it) ?: return@forEach
			byDiscord[discordId] = it.uniqueId
			byPlayer[it.uniqueId] = discordId
		}
	}

	private fun denizenGetDiscordId(offlinePlayer: OfflinePlayer): String? {
		val discordId = denizenParseTag("<[player].flag[discord].get[user].id.if_null[null]>", mapOf(
			Pair("player", PlayerTag(offlinePlayer))
		)).asElement().asString()
		if (discordId.isBlank() || discordId == "null") return null
		return discordId
	}

	private inner class DiscordLinkingTrackerListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			val discordId = denizenGetDiscordId(e.player)
			set(e.player, discordId)
		}
	}
}
