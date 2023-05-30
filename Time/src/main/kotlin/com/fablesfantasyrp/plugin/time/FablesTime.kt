package com.fablesfantasyrp.plugin.time

import com.fablesfantasyrp.plugin.time.command.Commands
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
import org.bukkit.ChatColor.*
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


internal val SYSPREFIX = "$GOLD${BOLD}[${RED}${BOLD} TIME ${GOLD}${BOLD}]${GRAY}"
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

		val injector = Intake.createInjector()
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
