package com.fablesfantasyrp.plugin.shops.appstart

import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.plugin.shops.command.ShopCommand
import com.fablesfantasyrp.plugin.shops.command.provider.ShopModule
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.Command
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
		val shops = mockk<ShopRepository>()

		every { Bukkit.getServer() } returns server
		every { registerCommand(any(), any(), any(), any()) } returns mockk<Command>()
		every { plugin.server } returns server

		val shopModule = ShopModule(shops)
		val commands = ShopCommand(shops)

		val config = CommandConfig(plugin, shopModule, commands)
		config.init()
		config.cleanup()
	}
}
