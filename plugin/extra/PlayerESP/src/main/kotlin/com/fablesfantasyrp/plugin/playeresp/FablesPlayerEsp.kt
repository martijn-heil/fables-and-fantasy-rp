package com.fablesfantasyrp.plugin.playeresp

import com.fablesfantasyrp.plugin.playeresp.command.Commands
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.gitlab.martijn_heil.nincommands.common.bukkit.unregisterCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.command.Command
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module


val SYSPREFIX = GLOBAL_SYSPREFIX

class FablesPlayerEsp : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesPlayerEsp } binds(arrayOf(JavaPlugin::class))
			single {
				val tmp = PlayerEspManager(get(), get())
				tmp.start()
				tmp
			}
			singleOf(::Commands)
		}
		loadKoinModules(koinModule)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(get<Commands>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		if (server.pluginManager.isPluginEnabled("FablesStaffMode")) {
			com.fablesfantasyrp.plugin.playeresp.interop.staffmode.StaffModeHook(get(), get()).start()
		}
	}

	override fun onDisable() {
		get<PlayerEspManager>().stop()
		commands.forEach { unregisterCommand(it) }
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesPlayerEsp
	}
}
