package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

class FablesHacks : JavaPlugin() {

	override fun onEnable() {
		val perms = server.servicesManager.getRegistration(Permission::class.java)!!
		val chat = server.servicesManager.getRegistration(Chat::class.java)!!

		server.servicesManager.register(Chat::class.java,
				HackyVaultChat(chat.provider, perms.provider), this, ServicePriority.Highest)
	}
}
