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
package com.fablesfantasyrp.plugin.characters.test.appstart

import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.plugin.characters.CharacterAuthorizer
import com.fablesfantasyrp.plugin.characters.appstart.CommandConfig
import com.fablesfantasyrp.plugin.characters.command.CharacterTraitCommand
import com.fablesfantasyrp.plugin.characters.command.Commands
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.service.api.CharacterSlotCountCalculator
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.ProfilePrompter
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
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
		val characters = mockk<CharacterRepository>()
		val profiles = mockk<ProfileRepository>()
		val profileManager = mockk<ProfileManager>()
		val profilePrompter = mockk<ProfilePrompter>()
		val authorizer = mockk<CharacterAuthorizer>()
		val characterSlotCountCalculator = mockk<CharacterSlotCountCalculator>()
		val profileProvider = mockk<Provider<Profile>>()

		every { Bukkit.getServer() } returns server
		every { registerCommand(any(), any(), any(), any()) } returns mockk<Command>()
		every { plugin.server } returns server

		val characterModule = CharacterModule(server, characters, profileManager, profileProvider)

		val commands = Commands(plugin, characters, profiles, profileManager, profilePrompter, authorizer, characterSlotCountCalculator)
		val commandsCharacters = commands.Characters()
		val commandsCharactersStats = commandsCharacters.Stats()
		val commandsCharactersChange = commandsCharacters.Change()
		val commandCharacterTrait = CharacterTraitCommand()
		val commandCharacterTraitCommand = commandCharacterTrait.CharacterTraitCommand()

		val config = CommandConfig(
			plugin,
			characterModule,
			commands,
			commandsCharacters,
			commandsCharactersStats,
			commandsCharactersChange,
			commandCharacterTrait,
			commandCharacterTraitCommand)

		config.init()
		config.cleanup()
	}
}
