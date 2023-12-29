package com.fablesfantasyrp.plugin.tools

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
import com.fablesfantasyrp.plugin.tools.command.Commands
import com.fablesfantasyrp.plugin.tools.command.InventoryCommands
import com.fablesfantasyrp.plugin.tools.command.provider.ToolsModule
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.caturix.spigot.common.CommonModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.BukkitAuthorizer
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.BukkitModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.caturix.Caturix
import com.fablesfantasyrp.caturix.fluent.CommandGraph
import com.fablesfantasyrp.caturix.parametric.ParametricBuilder
import com.fablesfantasyrp.caturix.parametric.provider.PrimitivesModule
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import org.bukkit.command.Command
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesTools.instance

class FablesTools : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(createdAtStart = true) {
			single <Plugin> { this@FablesTools } binds(arrayOf(JavaPlugin::class))

			singleOf(::PowerToolManager)
			singleOf(::BackManager)
			singleOf(::Commands)
			singleOf(::ToolsListener)
			singleOf(::InventoryCommands)
			single { get<Commands>().Ptime() }
			single { get<Commands>().PWeather() }

			factoryOf(::ToolsModule)
		}
		loadKoinModules(koinModule)

		get<PowerToolManager>().start()

		server.pluginManager.registerEvents(get<ToolsListener>(), this)

		val injector = Caturix.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<ProfileModule>())
		injector.install(get<CharacterModule>())
		injector.install(get<ToolsModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(get<Commands>())
		rootDispatcherNode.group("ptime", "playertime", "fptime", "fplayertime").registerMethods(get<Commands.Ptime>())
		rootDispatcherNode.group("pweather", "playerweather", "fpweather", "fplayerweather").registerMethods(get<Commands.PWeather>())
		rootDispatcherNode.registerMethods(get<InventoryCommands>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { command ->
			registerCommand(command.callable, this, command.allAliases.toList()) { this.launch(block = it) }
		}
	}

	override fun onDisable() {
		get<PowerToolManager>().stop()
		commands.forEach { unregisterCommand(it) }
		unloadKoinModules(koinModule)
	}


	companion object {
		lateinit var instance: FablesTools
	}
}
