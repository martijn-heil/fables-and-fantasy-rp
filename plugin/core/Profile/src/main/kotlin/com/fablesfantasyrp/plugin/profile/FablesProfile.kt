package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.profile.command.Commands
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileProvider
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepositoryImpl
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.profile.data.persistent.H2ProfileRepository
import com.fablesfantasyrp.plugin.profile.web.WebHook
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.OfflinePlayerProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.PlayerProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.gitlab.martijn_heil.nincommands.common.bukkit.unregisterCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesProfile.instance


class FablesProfile : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var koinModule: Module


	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_PROFILE", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesProfile } binds(arrayOf(JavaPlugin::class))

			single<EntityProfileRepository> {
				val profilesImpl = EntityProfileRepositoryImpl(H2ProfileRepository(server, fablesDatabase))
				profilesImpl.init()
				profilesImpl
			} bind ProfileRepository::class

			singleOf(::ProfileManagerImpl) bind ProfileManager::class
			singleOf(::SimpleProfilePrompter) bind ProfilePrompter::class
			singleOf(::ProfileListener)

			factory {
				ProfileProvider(
						get<EntityProfileRepository>(),
						get<ProfileManager>(),
						PlayerProvider(get<Server>(), OfflinePlayerProvider(get<Server>())),
						get<Server>()
					)
			} bind Provider::class withOptions { named("Profile") }
			factoryOf(::Commands)

			factory {
				ProfileModule(
						get<EntityProfileRepository>(),
						get<ProfileManager>(),
						BukkitSenderProvider(Player::class.java),
						PlayerProvider(get<Server>(), OfflinePlayerProvider(get<Server>())),
						get<Server>()
				)
			}
			factory { get<Commands>().CommandProfile() }
		}
		loadKoinModules(koinModule)

		server.servicesManager.register(EntityProfileRepository::class.java, get(), this, ServicePriority.Normal)
		server.servicesManager.register(ProfileManager::class.java, get(), this, ServicePriority.Normal)

		Services.register(ProfilePrompter::class, get(), this, ServicePriority.Low)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<ProfileModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("profile", "prof", "p").registerMethods(get<Commands.CommandProfile>())
		rootDispatcherNode.registerMethods(get<Commands>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(get<ProfileListener>(), this)

		if (server.pluginManager.isPluginEnabled("FablesWeb")) {
			try {
				logger.info("Enabling FablesWeb integration")
				WebHook().start()
			} catch (ex: Exception) {
				ex.printStackTrace()
				logger.warning("An error occurred during setup of FablesWeb integration.")
			}
		}
	}

	override fun onDisable() {
		get<EntityProfileRepository>().saveAllDirty()
		commands.forEach { unregisterCommand(it) }
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesProfile
	}
}
