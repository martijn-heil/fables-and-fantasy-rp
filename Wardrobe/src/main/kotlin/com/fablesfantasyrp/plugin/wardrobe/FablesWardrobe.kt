package com.fablesfantasyrp.plugin.wardrobe

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.plugin.wardrobe.data.ProfileSkinRepository
import com.fablesfantasyrp.plugin.wardrobe.data.SkinRepository
import com.fablesfantasyrp.plugin.wardrobe.data.persistent.H2ProfileSkinRepository
import com.fablesfantasyrp.plugin.wardrobe.data.persistent.H2SkinRepository
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

internal val SYSPREFIX = "${GOLD}${BOLD}[${LIGHT_PURPLE}${BOLD} WARDROBE ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesWardrobe.instance

class FablesWardrobe : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_WARDROBE", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}


		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesWardrobe } binds(arrayOf(JavaPlugin::class))

			single { H2SkinRepository(get(), fablesDatabase) } bind SkinRepository::class
			single { H2ProfileSkinRepository(fablesDatabase, get(), get()) } bind ProfileSkinRepository::class

			single {
				val originalProfileTrackerService = OriginalPlayerProfileServiceImpl(get())
				originalProfileTrackerService.init()
				originalProfileTrackerService
			} bind OriginalPlayerProfileService::class

			single { SkinSlotCountService(get(), Services.get()) }

			singleOf(::SkinServiceImpl) bind SkinService::class
			singleOf(::WardrobeListener)
			singleOf(::Commands)
		}
		loadKoinModules(koinModule)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<ProfileModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(get<Commands>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(get<WardrobeListener>(), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesWardrobe
	}
}
