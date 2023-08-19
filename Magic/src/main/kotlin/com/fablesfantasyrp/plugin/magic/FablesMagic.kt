package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.magic.command.Commands
import com.fablesfantasyrp.plugin.magic.command.provider.MagicModule
import com.fablesfantasyrp.plugin.magic.data.MapTearRepository
import com.fablesfantasyrp.plugin.magic.data.SimpleSpellDataRepository
import com.fablesfantasyrp.plugin.magic.data.entity.EntityMageRepository
import com.fablesfantasyrp.plugin.magic.data.entity.EntityTearRepository
import com.fablesfantasyrp.plugin.magic.data.persistent.H2MageRepository
import com.fablesfantasyrp.plugin.magic.data.persistent.YamlSimpleSpellDataRepository
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
import org.koin.dsl.binds
import org.koin.dsl.module

val SYSPREFIX = GLOBAL_SYSPREFIX
val MAX_TEARS_PER_MAGE = 3

val PLUGIN: FablesMagic get() = FablesMagic.instance

lateinit var tearRepository: EntityTearRepository<*>
lateinit var spellRepository: SimpleSpellDataRepository
lateinit var mageRepository: EntityMageRepository<*>


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

		spellRepository = YamlSimpleSpellDataRepository(this, spellsDirectory)
		mageRepository = EntityMageRepository(this, H2MageRepository(server, fablesDatabase))
		mageRepository.init()
		tearRepository = EntityTearRepository(MapTearRepository())
		tearRepository.all().forEach { it.spawn() }

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesMagic } binds(arrayOf(JavaPlugin::class))

			single { spellRepository }
			single { mageRepository }
			single { tearRepository }

			singleOf(::TearClosureManager)

			factoryOf(::Commands)
			factory { get<Commands>().Ability() }
			factoryOf(::MagicModule)
		}
		loadKoinModules(koinModule)

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

		server.scheduler.scheduleSyncRepeatingTask(this, {
			logger.info("Saving mages..")
			mageRepository.saveAllDirty()

			logger.info("Saving tears..")
			tearRepository.saveAllDirty()
		}, 0, 6000)
	}

	override fun onDisable() {
		mageRepository.saveAllDirty()
		tearRepository.saveAllDirty()
		tearRepository.all().forEach { it.despawn() }
		commands.forEach { unregisterCommand(it) }
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesMagic
	}
}
