package com.fablesfantasyrp.plugin.activity.appstart

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
import com.fablesfantasyrp.plugin.activity.command.Commands
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.command.Command
import org.bukkit.plugin.Plugin

internal class CommandConfig(private val plugin: Plugin,
							 private val commands: Commands) {
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

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(commands)

		return rootDispatcherNode.dispatcher
	}
}
