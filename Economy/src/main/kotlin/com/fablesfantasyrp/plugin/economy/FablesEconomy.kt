package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepository
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepositoryImpl
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.economy.data.persistent.H2ProfileEconomyRepository
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
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
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.binds
import org.koin.dsl.module

internal val PLUGIN get() = FablesEconomy.instance

const val CURRENCY_SYMBOL = "Ⓐ"

internal val SYSPREFIX = "$GOLD$BOLD[$LIGHT_PURPLE$BOLD ECONOMY $GOLD$BOLD]$GRAY"


class FablesEconomy : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var profileEconomyRepository: EntityProfileEconomyRepositoryImpl<*>

	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_ECONOMY", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesEconomy } binds(arrayOf(JavaPlugin::class))

			single {
				val profileEconomyRepository = EntityProfileEconomyRepositoryImpl(H2ProfileEconomyRepository(fablesDatabase))
				profileEconomyRepository.init()
				profileEconomyRepository
			} binds (arrayOf(EntityProfileEconomyRepository::class, ProfileEconomyRepository::class))

			factoryOf(::Commands)
			factory { get<Commands>().Eco() }
			factory { get<Commands>().Bank() }
		}
		loadKoinModules(koinModule)

		server.servicesManager.register(EntityProfileEconomyRepository::class.java, get(), this, ServicePriority.Normal)
		profileEconomyRepository = get()

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<ProfileModule>())
		injector.install(get<CharacterModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("feco").registerMethods(get<Commands.Eco>())
		rootDispatcherNode.group("bank").registerMethods(get<Commands.Bank>())
		rootDispatcherNode.registerMethods(get<Commands>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		//migrate(this, BankAPI.getInstance(), profileEconomyRepository, characterRepository)

		val vault = server.pluginManager.getPlugin("Vault")
		if (vault != null) {
			logger.info("Enabling Vault integration")
			com.fablesfantasyrp.plugin.economy.interop.vault.VaultHook(server, get(), this)
		}

		if (server.pluginManager.isPluginEnabled("Citizens")) {
			logger.info("Enabling Citizens integration")
			com.fablesfantasyrp.plugin.economy.interop.citizens.CitizensHook().start()
		}
	}

	override fun onDisable() {
		profileEconomyRepository.saveAll()
		commands.forEach { unregisterCommand(it) }
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesEconomy
	}
}
