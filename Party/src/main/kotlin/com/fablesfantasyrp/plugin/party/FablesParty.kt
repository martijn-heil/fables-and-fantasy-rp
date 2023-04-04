package com.fablesfantasyrp.plugin.party

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.party.auth.PartyAuthorizer
import com.fablesfantasyrp.plugin.party.auth.PartyAuthorizerImpl
import com.fablesfantasyrp.plugin.party.command.Commands
import com.fablesfantasyrp.plugin.party.command.provider.PartyModule
import com.fablesfantasyrp.plugin.party.data.MapPartyRepository
import com.fablesfantasyrp.plugin.party.data.PartyRepository
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
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module


internal val SYSPREFIX = "$GOLD${BOLD}[${GREEN}${BOLD} PARTY ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesParty.instance

class FablesParty : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesParty } binds(arrayOf(JavaPlugin::class))
			singleOf(::Commands)
			single { get<Commands>().PartyCommand() }
			single<PartyRepository> { MapPartyRepository() }
			singleOf(::PartyModule)
			singleOf(::PartyAuthorizerImpl) bind PartyAuthorizer::class
			singleOf(::PartyListener)
			singleOf(::PartySpectatorManager)
		}
		loadKoinModules(koinModule)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<PartyModule>())
		injector.install(get<CharacterModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(get<Commands>())
		rootDispatcherNode.group("party", "pa").registerMethods(get<Commands.PartyCommand>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(get<PartyListener>(), this)

		if (server.pluginManager.isPluginEnabled("TAB")) {
			com.fablesfantasyrp.plugin.party.interop.tab.TABHook(get(), get(), get(), get()).start()
		}
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesParty
			private set
	}
}
