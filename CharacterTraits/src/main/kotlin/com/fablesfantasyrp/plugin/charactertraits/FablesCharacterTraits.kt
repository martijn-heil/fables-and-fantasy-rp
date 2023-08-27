package com.fablesfantasyrp.plugin.charactertraits

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.charactertraits.command.Commands
import com.fablesfantasyrp.plugin.charactertraits.command.provider.CharacterTraitModule
import com.fablesfantasyrp.plugin.charactertraits.dal.h2.H2CharacterTraitDataRepository
import com.fablesfantasyrp.plugin.charactertraits.dal.repository.CharacterTraitDataRepository
import com.fablesfantasyrp.plugin.charactertraits.domain.mapper.CharacterTraitMapper
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepositoryImpl
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
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
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module


internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesCharacterTraits.instance

class FablesCharacterTraits : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_CHARACTER_TRAITS", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesCharacterTraits } binds (arrayOf(JavaPlugin::class))

			single { H2CharacterTraitDataRepository(fablesDatabase) } bind CharacterTraitDataRepository::class
			singleOf(::CharacterTraitMapper)
			singleOf(::CharacterTraitRepositoryImpl) bind CharacterTraitRepository::class

			singleOf(::Commands)
			factoryOf(::CharacterTraitModule)
		}
		loadKoinModules(koinModule)
		get<CharacterTraitRepositoryImpl>().init()

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<CharacterModule>())
		injector.install(get<CharacterTraitModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(get<Commands>())
		rootDispatcherNode.group("charactertrait").registerMethods(get<Commands>().CharacterTraitCommand())

		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.scheduler.scheduleSyncRepeatingTask(this, {
			logger.info("Saving character traits..")
			get<CharacterTraitRepositoryImpl>().saveAllDirty()
		}, 0, 6000)
	}

	override fun onDisable() {
		get<CharacterTraitRepositoryImpl>().saveAllDirty()
		unloadKoinModules(koinModule)
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesCharacterTraits
			private set
	}
}
