package com.fablesfantasyrp.plugin.time

import com.fablesfantasyrp.plugin.time.command.Commands
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
import org.bukkit.command.Command
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import java.time.Instant


internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesTime.instance

class FablesTime : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module
	private lateinit var synchronizer: WorldTimeSynchronizer
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		saveDefaultConfig()
		val time = Instant.ofEpochMilli(config.getLong("time"))
		val speed = config.getInt("speed")
		val worlds = config.getStringList("worlds").mapNotNull { server.getWorld(it) }

		koinModule = module(createdAtStart = true) {
			single <Plugin> { this@FablesTime } binds arrayOf(JavaPlugin::class)

			single {
				val clock = GameClock(get(), time, speed)
				clock.start()
				clock
			} bind FablesInstantSource::class

			singleOf(::Commands)
		}
		loadKoinModules(koinModule)

		synchronizer = WorldTimeSynchronizer(this, worlds, get())
		synchronizer.start()

		val injector = Caturix.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(get<Commands>())
		rootDispatcherNode.group("datetime").registerMethods(get<Commands>().DateTime())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }
	}

	override fun onDisable() {
		val clock = get<GameClock>()
		clock.stop()
		synchronizer.stop()
		config.set("time", clock.instant().toEpochMilli())
		saveConfig()
		commands.forEach { unregisterCommand(it) }
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesTime
			private set
	}
}
