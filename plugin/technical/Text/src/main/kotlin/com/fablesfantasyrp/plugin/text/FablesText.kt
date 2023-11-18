package com.fablesfantasyrp.plugin.text

import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.chat.Chat
import org.bukkit.plugin.java.JavaPlugin

lateinit var vaultChat: Chat
lateinit var miniMessage: MiniMessage

class FablesText : JavaPlugin() {

	override fun onEnable() {
		instance = this

		vaultChat = server.servicesManager.getRegistration(Chat::class.java)!!.provider
		miniMessage = MiniMessage.builder().strict(true).build()
	}

	companion object {
		lateinit var instance: FablesText
	}
}
