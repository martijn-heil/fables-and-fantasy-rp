/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.caturix.Caturix
import com.fablesfantasyrp.caturix.fluent.CommandGraph
import com.fablesfantasyrp.caturix.parametric.ParametricBuilder
import com.fablesfantasyrp.caturix.parametric.provider.PrimitivesModule
import com.fablesfantasyrp.caturix.spigot.common.CommonModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.BukkitAuthorizer
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.BukkitModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepository
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepositoryImpl
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.economy.data.persistent.H2ProfileEconomyRepository
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.launch
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
const val CURRENCY_NAME = "Andros"

internal val SYSPREFIX = GLOBAL_SYSPREFIX


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
				val profileEconomyRepository = EntityProfileEconomyRepositoryImpl(H2ProfileEconomyRepository(get(), get()))
				profileEconomyRepository.init()
				profileEconomyRepository
			} binds (arrayOf(EntityProfileEconomyRepository::class, ProfileEconomyRepository::class))

			factoryOf(::Commands)
			factory { get<Commands>().Eco() }
			factory { get<Commands>().Bank() }
			factory { get<Commands>().PlayerEco() }
		}
		loadKoinModules(koinModule)

		server.servicesManager.register(EntityProfileEconomyRepository::class.java, get(), this, ServicePriority.Normal)
		profileEconomyRepository = get()

		val injector = Caturix.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<ProfileModule>())
		injector.install(get<CharacterModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("eco", "feco").registerMethods(get<Commands.Eco>())
		rootDispatcherNode.group("playereco", "fplayereco").registerMethods(get<Commands.PlayerEco>())
		rootDispatcherNode.group("bank", "fbank").registerMethods(get<Commands.Bank>())
		rootDispatcherNode.registerMethods(get<Commands>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { command ->
			registerCommand(command.callable, this, command.allAliases.toList()) { this.launch(block = it) }
		}

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
