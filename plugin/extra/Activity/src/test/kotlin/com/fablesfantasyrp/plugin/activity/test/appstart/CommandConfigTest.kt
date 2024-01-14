package com.fablesfantasyrp.plugin.activity.test.appstart

import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.plugin.activity.appstart.CommandConfig
import com.fablesfantasyrp.plugin.activity.command.Commands
import com.fablesfantasyrp.plugin.activity.domain.repository.ActivityRegionRepository
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
		val activityRegionRepository = mockk<ActivityRegionRepository>()

		every { Bukkit.getServer() } returns server
		every { registerCommand(any(), any(), any(), any()) } returns mockk<Command>()
		every { plugin.server } returns server

		val commands = Commands(activityRegionRepository)

		val config = CommandConfig(plugin, commands)

		config.init()
		config.cleanup()
	}
}
