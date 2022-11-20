package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.persistent.H2PlayerInstanceRepository
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
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
import org.bukkit.entity.Player

val SYSPREFIX = "${GOLD}${BOLD}[${LIGHT_PURPLE}${BOLD} PLAYER INSTANCE ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesPlayerInstance.instance

private lateinit var playerInstanceManager: PlayerInstanceManager
lateinit var playersInstances: PlayerInstanceRepository
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

		playersInstances = EntityPlayerInstanceRepository(H2PlayerInstanceRepository(server, fablesDatabase))
		playerInstanceManager = PlayerInstanceManager()

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("playerinstance").registerMethods(Commands.PlayerInstance(playersInstances))
		rootDispatcherNode.registerMethods(Commands())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(PlayerInstanceListener(), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesPlayerInstance
	}
}

var Player.currentPlayerInstance
	get() = playerInstanceManager.getCurrentForPlayer(this)
	set(value) {
		if (value != null) {
			playerInstanceManager.setCurrentForPlayer(this, value)
		} else {
			playerInstanceManager.stopTracking(this)
		}
	}
