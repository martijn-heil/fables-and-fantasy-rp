package com.fablesfantasyrp.plugin.fasttravel

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.fasttravel.data.entity.EntityFastTravelLinkRepository
import com.fablesfantasyrp.plugin.fasttravel.data.entity.FastTravelLinkRepository
import com.fablesfantasyrp.plugin.fasttravel.data.entity.FastTravelPlayerRepository
import com.fablesfantasyrp.plugin.fasttravel.data.entity.MapFastTravelPlayerRepository
import com.fablesfantasyrp.plugin.fasttravel.data.persistent.H2FastTravelLinkRepository
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.plugin.worldguardinterop.command.WorldGuardModule
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
import com.sk89q.worldguard.WorldGuard
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin

val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesFastTravel.instance
internal val regionContainer get() = WorldGuard.getInstance().platform.regionContainer

class FablesFastTravel : JavaPlugin() {
	private lateinit var commands: Collection<Command>
	lateinit var links: FastTravelLinkRepository
		private set
	lateinit var players: FastTravelPlayerRepository
		private set

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_FASTTRAVEL", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		players = MapFastTravelPlayerRepository()
		links = run {
			val repo = EntityFastTravelLinkRepository(H2FastTravelLinkRepository(server, fablesDatabase, regionContainer))
			repo.init()
			repo
		}

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(WorldGuardModule(server, regionContainer))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("fasttravel").registerMethods(Commands.FastTravel(links))
		rootDispatcherNode.registerMethods(Commands(links))
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(FastTravelListener(links), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesFastTravel
	}
}
