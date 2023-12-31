package com.fablesfantasyrp.plugin.shops.test.appstart

import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.OfflinePlayerProvider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.PlayerProvider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderProvider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.shops.ShopAuthorizer
import com.fablesfantasyrp.plugin.shops.ShopSlotCountCalculator
import com.fablesfantasyrp.plugin.shops.appstart.CommandConfig
import com.fablesfantasyrp.plugin.shops.command.ShopCommand
import com.fablesfantasyrp.plugin.shops.command.provider.ShopModule
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Test

internal class CommandConfigTest {
	@Test
	fun testCommandConfig() {
		mockkStatic(::registerCommand)
		mockkStatic(::unregisterCommand)
		mockkStatic(Bukkit::class)

		val plugin = mockk<JavaPlugin>()
		val server = mockk<Server>()
		val profileManager = mockk<ProfileManager>()
		val shopAuthorizer = mockk<ShopAuthorizer>()
		val shopSlotCountCalculator = mockk<ShopSlotCountCalculator>()
		val shops = mockk<ShopRepository>()
		val profiles = mockk<EntityProfileRepository>()

		every { Bukkit.getServer() } returns server
		every { registerCommand(any(), any(), any(), any()) } returns mockk<Command>()
		every { plugin.server } returns server

		val offlinePlayerProvider = OfflinePlayerProvider(server)
		val playerProvider = PlayerProvider(server, offlinePlayerProvider)
		val senderProvider = BukkitSenderProvider(Player::class.java)
		val profileModule = ProfileModule(profiles, profileManager, senderProvider, playerProvider, server)
		val shopModule = ShopModule(shops)
		val commands = ShopCommand(shops, shopAuthorizer, shopSlotCountCalculator)

		val config = CommandConfig(plugin, profileModule, shopModule, commands)
		config.init()
		config.cleanup()
	}
}
