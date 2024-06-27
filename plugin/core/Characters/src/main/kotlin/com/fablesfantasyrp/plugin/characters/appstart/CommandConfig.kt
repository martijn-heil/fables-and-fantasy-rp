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
package com.fablesfantasyrp.plugin.characters.appstart

import com.fablesfantasyrp.caturix.Caturix
import com.fablesfantasyrp.caturix.dispatcher.Dispatcher
import com.fablesfantasyrp.caturix.fluent.CommandGraph
import com.fablesfantasyrp.caturix.parametric.ParametricBuilder
import com.fablesfantasyrp.caturix.parametric.provider.PrimitivesModule
import com.fablesfantasyrp.caturix.spigot.common.CommonModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.BukkitAuthorizer
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.BukkitModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.plugin.characters.command.CharacterTraitCommand
import com.fablesfantasyrp.plugin.characters.command.Commands
import com.fablesfantasyrp.plugin.characters.command.LegacyCommands
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.command.Command
import org.bukkit.plugin.Plugin

internal class CommandConfig(private val plugin: Plugin,
							 private val characterModule: CharacterModule,
							 private val commands: Commands,
							 private val commandsCharacters: Commands.Characters,
							 private val commandsCharactersStats: Commands.Characters.Stats,
							 private val commandsCharactersChange: Commands.Characters.Change,
							 private val commandCharacterTrait: CharacterTraitCommand,
							 private val commandCharacterTraitCommand: CharacterTraitCommand.CharacterTraitCommand) {
	private val server = plugin.server
	private lateinit var bukkitCommands: Collection<Command>

	fun init() {
		val dispatcher = makeDispatcher()

		bukkitCommands = dispatcher.commands.mapNotNull { command ->
			registerCommand(command.callable, plugin, command.allAliases.toList()) { plugin.launch(block = it) }
		}
	}

	fun cleanup() {
		bukkitCommands.forEach { unregisterCommand(it) }
	}

	internal fun makeDispatcher(): Dispatcher {
		val injector = Caturix.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(characterModule)

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(commands)

		val charactersCommand = rootDispatcherNode.group("character", "char", "fchar", "fcharacter")
		charactersCommand.registerMethods(commandsCharacters)
		rootDispatcherNode.registerMethods(LegacyCommands(commandsCharacters))

		charactersCommand.group("stats").registerMethods(commandsCharactersStats)
		charactersCommand.group("change").registerMethods(commandsCharactersChange)

		rootDispatcherNode.registerMethods(commandCharacterTrait)
		rootDispatcherNode.group("charactertrait").registerMethods(commandCharacterTraitCommand)

		return rootDispatcherNode.dispatcher
	}
}
