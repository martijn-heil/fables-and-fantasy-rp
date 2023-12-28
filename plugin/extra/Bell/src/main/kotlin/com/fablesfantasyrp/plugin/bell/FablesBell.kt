package com.fablesfantasyrp.plugin.bell

import com.fablesfantasyrp.plugin.bell.command.Commands
import com.fablesfantasyrp.plugin.bell.command.provider.BellModule
import com.fablesfantasyrp.plugin.bell.dal.h2.H2BellDataRepository
import com.fablesfantasyrp.plugin.bell.domain.mapper.BellMapper
import com.fablesfantasyrp.plugin.bell.domain.repository.BellRepository
import com.fablesfantasyrp.plugin.bell.domain.repository.BellRepositoryImpl
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.applyMigrations
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
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal val SYSPREFIX = GLOBAL_SYSPREFIX
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
				val h2Repository = H2BellDataRepository(get())
				val mapper = BellMapper(h2Repository)
				val characterRepositoryImpl = BellRepositoryImpl(mapper)
				characterRepositoryImpl.init()
				characterRepositoryImpl
			} bind BellRepository::class

			factoryOf(::BellModule)
			factoryOf(::Commands)
			factory { get<Commands>().BellCommand() }
		}
		loadKoinModules(koinModule)

		val injector = Caturix.createInjector()
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
		get<BellRepositoryImpl>().saveAllDirty()
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesBell
	}
}
