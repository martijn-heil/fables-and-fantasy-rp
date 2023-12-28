package com.fablesfantasyrp.plugin.basicsystem

import com.fablesfantasyrp.plugin.basicsystem.data.entity.EntityBasicSystemPlayerRepository
import com.fablesfantasyrp.plugin.basicsystem.data.entity.EntityBasicSystemPlayerRepositoryImpl
import com.fablesfantasyrp.plugin.basicsystem.data.entity.EntitySlidingDoorRepository
import com.fablesfantasyrp.plugin.basicsystem.data.entity.EntitySlidingDoorRepositoryImpl
import com.fablesfantasyrp.plugin.basicsystem.data.persistent.H2BasicSystemPlayerRepository
import com.fablesfantasyrp.plugin.basicsystem.data.persistent.H2SlidingDoorRepository
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.caturix.spigot.common.CommonModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.BukkitAuthorizer
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.BukkitModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.caturix.Caturix
import com.fablesfantasyrp.caturix.fluent.CommandGraph
import com.fablesfantasyrp.caturix.parametric.ParametricBuilder
import com.fablesfantasyrp.caturix.parametric.provider.PrimitivesModule
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin

internal val SYSPREFIX = "${GOLD}${BOLD}[${LIGHT_PURPLE}${BOLD} BASIC SYSTEM ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesBasicSystem.instance

class FablesBasicSystem : JavaPlugin() {
	private lateinit var commands: Collection<Command>
	lateinit var doors: EntitySlidingDoorRepository private set
	lateinit var players: EntityBasicSystemPlayerRepository private set

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_BASICSYSTEM", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		players = run {
			val repo = EntityBasicSystemPlayerRepositoryImpl(H2BasicSystemPlayerRepository(fablesDatabase, server), this)
			repo.init()
			repo
		}

		doors = run {
			val repo = EntitySlidingDoorRepositoryImpl(H2SlidingDoorRepository(server, fablesDatabase))
			repo.init()
			repo
		}

		val injector = Caturix.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("doors").registerMethods(Commands.Doors(doors))
		rootDispatcherNode.registerMethods(Commands(players))
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { command ->
			registerCommand(command.callable, this, command.allAliases.toList()) { this.launch(block = it) }
		}

		server.pluginManager.registerEvents(BasicSystemListener(players), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesBasicSystem
	}
}
