package com.fablesfantasyrp.plugin.economy.interop.vault

import com.fablesfantasyrp.plugin.profile.ProfileManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority

class VaultHook(server: Server,
				profileManager: ProfileManager,
				plugin: Plugin) {
	init {
		   server.servicesManager.register(
				   Economy::class.java,
				   VaultProfileEconomy(server, profileManager, plugin.logger),
				   plugin,
				   ServicePriority.Highest)
	}
}
