package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.utils.isVanished
import org.bukkit.ChatColor.*
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.LOW
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class WhitelistListener(private val server: Server) : Listener {
	@EventHandler(priority = LOW)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val p = e.player
		if (p.isVanished) return
		if (p.gameMode != GameMode.SURVIVAL) p.gameMode = GameMode.SURVIVAL
		if (!p.allowFlight) p.allowFlight = true
		e.player.sendMessage("$SYSPREFIX Welcome to ${GREEN}${BOLD}Fables & Fantasy${GRAY}!")
		e.player.sendMessage("")
		e.player.sendMessage("${GRAY}In order to play, you must apply for ${YELLOW}Whitelist Access${GRAY}!")
		e.player.sendMessage("${GRAY}This is a ${YELLOW}very quick and simple${GRAY} process!")
		e.player.sendMessage("${GRAY}Your application will be reviewed ${YELLOW}in 1 day or less${GRAY}.")
		e.player.sendMessage("https://forums.fablesfantasyrp.com/index.php?form/whitelist-application.1/select")
		e.player.sendMessage("")
		e.player.sendMessage("${GRAY}If you have ${YELLOW}any questions${GRAY}, please feel free to ask on our ${YELLOW}Discord${GRAY}!")
		e.player.sendMessage("https://discord.gg/ymNFxDKPx9")
		e.player.sendMessage("${YELLOW}Type in chat ${GRAY}to talk with other spectators and staff.")
		e.player.sendMessage("${GRAY}Please enjoy your stay in the world of Eden!")
	}

	fun onPlayerWhitelisted(offlinePlayer: OfflinePlayer) {
		server.dispatchCommand(server.consoleSender, "sv off ${offlinePlayer.name}")
		if (offlinePlayer.isOnline) {
			val player = offlinePlayer.player!!
			player.teleport(player.location.world.spawnLocation)
			player.gameMode = GameMode.SURVIVAL
			player.allowFlight = false
			player.kickPlayer("You have been whitelisted, please relog!")
		}
	}

	fun onPlayerUnWhitelisted(offlinePlayer: OfflinePlayer) {

	}
}