package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.characters.characterRepository
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepository
import com.fablesfantasyrp.plugin.economy.data.entity.EntityProfileEconomyRepositoryImpl
import com.fablesfantasyrp.plugin.economy.data.persistent.H2ProfileEconomyRepository
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
import com.fablesfantasyrp.plugin.profile.profileManager
import com.fablesfantasyrp.plugin.profile.profiles
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.plugin.utils.essentialsSpawn
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.gitlab.martijn_heil.nincommands.common.bukkit.unregisterCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import net.milkbowl.vault.economy.Economy
import org.bukkit.ChatColor.*
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicePriority

internal val PLUGIN get() = FablesEconomy.instance

const val CURRENCY_SYMBOL = "Ⓐ"

internal val SYSPREFIX = "$GOLD$BOLD[$LIGHT_PURPLE$BOLD ECONOMY $GOLD$BOLD]$GRAY"


class FablesEconomy : SuspendingJavaPlugin() {
	private lateinit var commands: Collection<Command>

	lateinit var profileEconomyRepository: EntityProfileEconomyRepositoryImpl<*>
		private set

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_ECONOMY", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		val defaultLocation: Location = essentialsSpawn.getSpawn("default").toCenterLocation()

		profileEconomyRepository = EntityProfileEconomyRepositoryImpl(H2ProfileEconomyRepository(fablesDatabase))
		profileEconomyRepository.init()

		server.servicesManager.register(
				Economy::class.java,
				VaultProfileEconomy(server, profileManager),
				this,
				ServicePriority.Highest
		)

		server.servicesManager.register(
				EntityProfileEconomyRepository::class.java,
				profileEconomyRepository,
				this,
				ServicePriority.Normal
		)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(ProfileModule(profiles, profileManager, BukkitSenderProvider(Player::class.java)))
		injector.install(Services.get<CharacterModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("feco").registerMethods(Commands.Eco(characterRepository))
		rootDispatcherNode.group("bank").registerMethods(Commands.Bank(this, profileEconomyRepository))
		rootDispatcherNode.registerMethods(Commands(characterRepository, profileManager))
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		//migrate(this, BankAPI.getInstance(), profileEconomyRepository, characterRepository)

		if (server.pluginManager.isPluginEnabled("Citizens")) {
			logger.info("Enabling Citizens integration")
			com.fablesfantasyrp.plugin.economy.interop.citizens.CitizensHook().start()
		}
	}

	override fun onDisable() {
		profileEconomyRepository.saveAll()
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesEconomy
	}
}
