package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.command.Commands
import com.fablesfantasyrp.plugin.characters.command.LegacyCommands
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.characters.data.entity.CharacterRepository
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepositoryImpl
import com.fablesfantasyrp.plugin.characters.data.persistent.H2CharacterRepository
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.profile.ProfilePrompter
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.gitlab.martijn_heil.nincommands.common.bukkit.unregisterCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal val SYSPREFIX = "$GOLD[ $GREEN${BOLD}CHARACTERS $GOLD] $GRAY"

internal val PLUGIN get() = FablesCharacters.instance

class FablesCharacters : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_CHARACTERS", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single <Plugin> { this@FablesCharacters } binds(arrayOf(JavaPlugin::class, SuspendingJavaPlugin::class))

			single<EntityCharacterRepository> {
				val h2CharacterRepository = H2CharacterRepository(get(), get(), get())
				val characterRepositoryImpl = EntityCharacterRepositoryImpl(h2CharacterRepository, get())
				characterRepositoryImpl.init()
				characterRepositoryImpl
			} bind CharacterRepository::class

			factory { CharacterModule(get(), get(), get(), get<Provider<Profile>>(named("Profile"))) }
			singleOf(::CharactersListener)
			singleOf(::CharactersLiveMigrationListener)
			singleOf(::CharacterCreationListener)
			singleOf(::Commands)
			single { get<Commands>().Characters() }
			single { get<Commands.Characters>().Change() }
			single { get<Commands.Characters>().Stats() }
			singleOf(::LegacyCommands)
		}
		loadKoinModules(koinModule)

		server.servicesManager.register(EntityCharacterRepository::class.java, get(), this, ServicePriority.Normal)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<CharacterModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val commandsInstance = get<Commands>()
		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(commandsInstance)

		val charactersCommand = rootDispatcherNode.group("character", "char", "fchar", "fcharacter")
		charactersCommand.registerMethods(commandsInstance.Characters())
		rootDispatcherNode.registerMethods(LegacyCommands(commandsInstance.Characters()))

		charactersCommand.group("stats").registerMethods(get<Commands.Characters.Stats>())
		charactersCommand.group("change").registerMethods(get<Commands.Characters.Change>())

		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(get<CharactersListener>(), this)
		server.pluginManager.registerEvents(get<CharactersLiveMigrationListener>(), this)
		server.pluginManager.registerEvents(get<CharacterCreationListener>(), this)

		if (server.pluginManager.isPluginEnabled("TAB") && server.pluginManager.isPluginEnabled("Denizen") ) {
			com.fablesfantasyrp.plugin.characters.nametags.NameTagManager(get(), get()).start()
		}
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
		get<EntityCharacterRepository>().saveAllDirty()
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}
