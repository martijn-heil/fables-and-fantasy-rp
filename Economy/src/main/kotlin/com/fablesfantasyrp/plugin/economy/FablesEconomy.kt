package com.fablesfantasyrp.plugin.economy

import com.fablesfantasyrp.plugin.characters.characterRepository
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.economy.data.entity.EntityPlayerInstanceEconomyRepository
import com.fablesfantasyrp.plugin.economy.data.entity.EntityPlayerInstanceEconomyRepositoryImpl
import com.fablesfantasyrp.plugin.economy.data.persistent.H2PlayerInstanceEconomyRepository
import com.fablesfantasyrp.plugin.playerinstance.command.provider.PlayerInstanceModule
import com.fablesfantasyrp.plugin.playerinstance.command.provider.PlayerInstanceProvider
import com.fablesfantasyrp.plugin.playerinstance.playerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.playerInstances
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
import me.dablakbandit.bank.api.BankAPI
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

	lateinit var playerInstanceEconomyRepository: EntityPlayerInstanceEconomyRepositoryImpl<*>
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

		playerInstanceEconomyRepository = EntityPlayerInstanceEconomyRepositoryImpl(
				H2PlayerInstanceEconomyRepository(fablesDatabase))
		playerInstanceEconomyRepository.init()

		server.servicesManager.register(
				Economy::class.java,
				VaultPlayerInstanceEconomy(server),
				this,
				ServicePriority.Highest
		)

		server.servicesManager.register(
				EntityPlayerInstanceEconomyRepository::class.java,
				playerInstanceEconomyRepository,
				this,
				ServicePriority.Normal
		)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(PlayerInstanceModule(playerInstances, playerInstanceManager, BukkitSenderProvider(Player::class.java)))
		injector.install(CharacterModule(server, characterRepository, PlayerInstanceProvider(playerInstances)))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("feco").registerMethods(Commands.Eco(characterRepository))
		rootDispatcherNode.group("bank").registerMethods(Commands.Bank(this, playerInstanceEconomyRepository))
		rootDispatcherNode.registerMethods(Commands(characterRepository))
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		migrate(server, BankAPI.getInstance(), playerInstanceEconomyRepository, characterRepository)
	}

	override fun onDisable() {
		playerInstanceEconomyRepository.saveAll()
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesEconomy
	}
}
