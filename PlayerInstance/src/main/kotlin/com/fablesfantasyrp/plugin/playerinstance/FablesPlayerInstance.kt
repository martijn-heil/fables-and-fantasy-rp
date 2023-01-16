package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.playerinstance.command.Commands
import com.fablesfantasyrp.plugin.playerinstance.command.provider.PlayerInstanceModule
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepositoryImpl
import com.fablesfantasyrp.plugin.playerinstance.data.persistent.H2PlayerInstanceRepository
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
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
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.plugin.ServicePriority

internal val SYSPREFIX = "${GOLD}${BOLD}[${LIGHT_PURPLE}${BOLD} PLAYER INSTANCE ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesPlayerInstance.instance

lateinit var playerInstanceManager: PlayerInstanceManager
lateinit var playerInstances: EntityPlayerInstanceRepository
	private set

class FablesPlayerInstance : SuspendingJavaPlugin() {
	private lateinit var commands: Collection<Command>


	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_PLAYERINSTANCE", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		val playerInstancesImpl = EntityPlayerInstanceRepositoryImpl(H2PlayerInstanceRepository(server, fablesDatabase))
		playerInstancesImpl.init()
		playerInstances = playerInstancesImpl
		server.servicesManager.register(EntityPlayerInstanceRepository::class.java, playerInstances, this, ServicePriority.Normal)

		playerInstanceManager = PlayerInstanceManagerImpl(server)
		Services.register(PlayerInstanceManager::class, playerInstanceManager, this, ServicePriority.Normal)

		val prompter = SimplePlayerInstanceSelectionPrompter(this)
		Services.register(PlayerInstanceSelectionPrompter::class, prompter, this, ServicePriority.Low)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(PlayerInstanceModule(playerInstances, playerInstanceManager, BukkitSenderProvider(Player::class.java)))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("playerinstance", "pi", "p").registerMethods(Commands.CommandPlayerInstance(playerInstances, playerInstanceManager))
		rootDispatcherNode.registerMethods(Commands())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(PlayerInstanceListener(this, playerInstances, playerInstanceManager), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesPlayerInstance
	}
}

/*var PlayerInstance.currentPlayer: Player?
	get() = playerInstanceManager.getCurrentForPlayerInstance(this)
	set(value) {
		require(value != null)
		playerInstanceManager.setCurrentForPlayer(value, this)
	}*/
