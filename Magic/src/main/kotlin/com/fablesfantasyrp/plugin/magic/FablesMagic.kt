package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.magic.authorizer.MagicTypeAuthorizer
import com.fablesfantasyrp.plugin.magic.authorizer.MagicTypeAuthorizerImpl
import com.fablesfantasyrp.plugin.magic.authorizer.SpellAuthorizer
import com.fablesfantasyrp.plugin.magic.authorizer.SpellAuthorizerImpl
import com.fablesfantasyrp.plugin.magic.command.Commands
import com.fablesfantasyrp.plugin.magic.command.provider.MagicModule
import com.fablesfantasyrp.plugin.magic.dal.repository.MageDataRepository
import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.dal.repository.h2.H2MageDataRepository
import com.fablesfantasyrp.plugin.magic.dal.repository.yaml.YamlSpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.mapper.MageMapper
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepositoryImpl
import com.fablesfantasyrp.plugin.magic.domain.repository.TearRepository
import com.fablesfantasyrp.plugin.magic.domain.repository.TearRepositoryImpl
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

val SYSPREFIX = GLOBAL_SYSPREFIX
val MAX_TEARS_PER_MAGE = 3

val PLUGIN: FablesMagic get() = FablesMagic.instance


class FablesMagic : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_MAGIC", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		val spellsDirectory = this.dataFolder.resolve("spells")
		spellsDirectory.mkdirs()

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesMagic } binds(arrayOf(JavaPlugin::class))

			single { YamlSpellDataRepository(get(), spellsDirectory) } bind SpellDataRepository::class
			single { H2MageDataRepository(fablesDatabase, get()) } bind MageDataRepository::class
			singleOf(::MageMapper)
			single { MageRepositoryImpl(get()).apply { init() } } bind MageRepository::class

			singleOf(::TearRepositoryImpl) bind TearRepository::class
			singleOf(::TearClosureManager)
			singleOf(::CastingTracker)

			singleOf(::SpellAuthorizerImpl) bind SpellAuthorizer::class
			singleOf(::MagicTypeAuthorizerImpl) bind MagicTypeAuthorizer::class

			factoryOf(::Commands)
			factory { get<Commands>().Ability() }
			factoryOf(::MagicModule)
		}
		loadKoinModules(koinModule)
		get<TearRepository>().all().forEach { it.spawn() }

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<CharacterModule>())
		injector.install(get<MagicModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("ability").registerMethods(get<Commands.Ability>())
		rootDispatcherNode.registerMethods(get<Commands>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		/*server.scheduler.scheduleSyncRepeatingTask(this, {
			logger.info("Saving mages..")
			get<MageRepositoryImpl>().saveAllDirty()
		}, 0, 6000)*/
	}

	override fun onDisable() {
		get<MageRepositoryImpl>().saveAllDirty()
		get<TearRepository>().all().forEach { it.despawn() }
		commands.forEach { unregisterCommand(it) }
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesMagic
	}
}
