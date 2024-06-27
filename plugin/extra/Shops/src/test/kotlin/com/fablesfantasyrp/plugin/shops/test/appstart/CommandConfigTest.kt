/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
		val shopModule = ShopModule(shops, senderProvider)
		val commands = ShopCommand(shops, shopAuthorizer, shopSlotCountCalculator)

		val config = CommandConfig(plugin, profileModule, shopModule, commands)
		config.init()
		config.cleanup()
	}
}
