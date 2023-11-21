package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.lodestones.command.LodestoneBannerCommand
import com.fablesfantasyrp.plugin.lodestones.command.LodestoneCommand
import com.fablesfantasyrp.plugin.lodestones.command.provider.LodestoneModule
import com.fablesfantasyrp.plugin.lodestones.dal.h2.H2CharacterLodestoneDataRepository
import com.fablesfantasyrp.plugin.lodestones.dal.h2.H2LodestoneBannerDataRepository
import com.fablesfantasyrp.plugin.lodestones.dal.h2.H2LodestoneDataRepository
import com.fablesfantasyrp.plugin.lodestones.domain.mapper.CharacterLodestoneMapper
import com.fablesfantasyrp.plugin.lodestones.domain.mapper.LodestoneBannerMapper
import com.fablesfantasyrp.plugin.lodestones.domain.mapper.LodestoneMapper
import com.fablesfantasyrp.plugin.lodestones.domain.repository.*
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
internal val PLUGIN get() = FablesLodestones.instance

class FablesLodestones : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_LODESTONES", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		saveDefaultConfig()

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesLodestones } binds(arrayOf(JavaPlugin::class))

			singleOf(::LodestoneListener)
			singleOf(::TeleportParticlesListener)

			single {
				val h2Repository = H2LodestoneDataRepository(get())
				val mapper = LodestoneMapper(h2Repository)
				val domainRepository = LodestoneRepositoryImpl(mapper)
				domainRepository.init()
				domainRepository
			} bind LodestoneRepository::class

			single {
				val h2Repository = H2LodestoneBannerDataRepository(get())
				val mapper = LodestoneBannerMapper(h2Repository, get())
				val domainRepository = LodestoneBannerRepositoryImpl(mapper, get())
				domainRepository.init()
				domainRepository
			} bind LodestoneBannerRepository::class

			single {
				val h2Repository = H2CharacterLodestoneDataRepository(get())
				val mapper = CharacterLodestoneMapper(get(), h2Repository)
				val domainRepository = CharacterLodestoneRepositoryImpl(mapper, get())
				domainRepository.init()
				domainRepository
			} bind CharacterLodestoneRepository::class

			single {
				val repo = YamlMapBoxRepository(get(), config, get())
				repo .init()
				repo
			} bind MapBoxRepository::class

			singleOf(::LodestoneBannerService)

			factoryOf(::LodestoneModule)
			factoryOf(::LodestoneCommand)
			factoryOf(::LodestoneBannerCommand)
			factoryOf(::LodestoneAuthorizerImpl) bind LodestoneAuthorizer::class
		}
		loadKoinModules(koinModule)

		get<LodestoneBannerService>().init()

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<CharacterModule>())
		injector.install(get<LodestoneModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("lodestone").registerMethods(get<LodestoneCommand>())
		rootDispatcherNode.group("lodebanner").registerMethods(get<LodestoneBannerCommand>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(get<LodestoneListener>(), this)
		server.pluginManager.registerEvents(get<TeleportParticlesListener>(), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
		get<LodestoneBannerService>().stop()
		get<LodestoneRepositoryImpl>().saveAllDirty()
		get<LodestoneBannerRepositoryImpl>().saveAllDirty()
		get<CharacterLodestoneRepositoryImpl>().saveAllDirty()
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesLodestones
	}
}
