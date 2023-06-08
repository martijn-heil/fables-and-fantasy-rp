package com.fablesfantasyrp.plugin.bell

import com.fablesfantasyrp.plugin.bell.command.Commands
import com.fablesfantasyrp.plugin.bell.command.provider.BellModule
import com.fablesfantasyrp.plugin.bell.data.entity.BellRepository
import com.fablesfantasyrp.plugin.bell.data.entity.EntityBellRepository
import com.fablesfantasyrp.plugin.bell.data.entity.EntityBellRepositoryImpl
import com.fablesfantasyrp.plugin.bell.data.persistent.H2BellRepository
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
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
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

internal val SYSPREFIX = "${GOLD}${BOLD}[${LIGHT_PURPLE}${BOLD} BELL ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesBell.instance

class FablesBell : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_BELL", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesBell } binds(arrayOf(JavaPlugin::class))

			singleOf(::BellListener)

			single {
				val repo = EntityBellRepositoryImpl(H2BellRepository(fablesDatabase))
				repo.init()
				repo
			} binds arrayOf(EntityBellRepository::class, BellRepository::class)

			factoryOf(::BellModule)
			factoryOf(::Commands)
			factory { get<Commands>().BellCommand() }
		}
		loadKoinModules(koinModule)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<CharacterModule>())
		injector.install(get<BellModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("bell").registerMethods(get<Commands.BellCommand>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(get<BellListener>(), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
		get<EntityBellRepository>().saveAllDirty()
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesBell
	}
}
