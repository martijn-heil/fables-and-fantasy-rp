package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.inventory.data.entity.EntityFablesInventoryRepository
import com.fablesfantasyrp.plugin.inventory.data.persistent.H2PlayerInstanceInventoryRepository
import com.fablesfantasyrp.plugin.playerinstance.currentPlayerInstance
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.sk89q.intake.Intake
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.command.Command

internal val PLUGIN get() = FablesInventoryPlugin.instance

class FablesInventoryPlugin : SuspendingJavaPlugin() {
	private lateinit var commands: Collection<Command>
	lateinit var inventories: EntityFablesInventoryRepository<*>
		private set

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_INVENTORY", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		inventories = EntityFablesInventoryRepository(H2PlayerInstanceInventoryRepository(fablesDatabase))

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

//		val rootDispatcherNode = CommandGraph().builder(builder).commands()
//		rootDispatcherNode.group("playerinstance").registerMethods(Commands.PlayerInstance())
//		rootDispatcherNode.registerMethods(Commands())
//		val dispatcher = rootDispatcherNode.dispatcher

//		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(PlayerInstanceInventoryListener(inventories), this)
	}

	override fun onDisable() {
		//commands.forEach { unregisterCommand(it) }
		server.onlinePlayers.forEach {
			val instance = it.currentPlayerInstance ?: return@forEach
			instance.inventory.delegate.bukkitInventory = null
		}
		inventories.saveAll()
	}

	companion object {
		lateinit var instance: FablesInventoryPlugin
	}
}
