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
package com.fablesfantasyrp.plugin.fasttravel

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
import com.github.shynixn.mccoroutine.bukkit.launch
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
			val repo = EntityFastTravelLinkRepository(H2FastTravelLinkRepository(this, fablesDatabase, regionContainer))
			repo.init()
			repo
		}

		val injector = Caturix.createInjector()
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

		commands = dispatcher.commands.mapNotNull { command ->
			registerCommand(command.callable, this, command.allAliases.toList()) { this.launch(block = it) }
		}

		server.pluginManager.registerEvents(FastTravelListener(links), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesFastTravel
	}
}
