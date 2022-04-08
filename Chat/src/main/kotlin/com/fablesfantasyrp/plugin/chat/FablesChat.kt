package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.playerdata.FablesOfflinePlayer
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin

internal val SYSPREFIX = "${GOLD}${BOLD}[${DARK_AQUA}${BOLD} CHAT ${GOLD}${BOLD}]"

class FablesChat : JavaPlugin() {

	override fun onEnable() {
		instance = this

//		val injector = Intake.createInjector()
//		injector.install(PrimitivesModule())
//		injector.install(BukkitModule(server))
//		injector.install(BukkitSenderModule())
//		injector.install(CommonModule())
//		injector.install(PlayerCharacterModule(server))
//
//		val builder = ParametricBuilder(injector)
//		builder.authorizer = BukkitAuthorizer()
//
//		val dispatcher = CommandGraph()
//				.builder(builder)
//				.commands()
//				.registerMethods(Commands())
//				.graph()
//				.dispatcher
//
//		registerCommand(dispatcher, this, dispatcher.aliases.toList())
	}

	companion object {
		lateinit var instance: FablesChat
	}
}

var FablesOfflinePlayer.chatChannel
	get() = rawData.chatChannel
	set(value) {
		rawData.chatChannel = value.lowercase()
		player?.sendMessage("$SYSPREFIX Your chat channel has been switched to ${value.uppercase()}!")
	}
