package com.fablesfantasyrp.plugin.characters.appstart

import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.plugin.characters.CharacterAuthorizer
import com.fablesfantasyrp.plugin.characters.command.CharacterTraitCommand
import com.fablesfantasyrp.plugin.characters.command.Commands
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
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
		val profileProvider = mockk<Provider<Profile>>()

		every { Bukkit.getServer() } returns server
		every { registerCommand(any(), any(), any(), any()) } returns mockk<Command>()
		every { plugin.server } returns server

		val characterModule = CharacterModule(server, characters, profileManager, profileProvider)

		val commands = Commands(plugin, characters, profiles, profileManager, profilePrompter, authorizer)
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
