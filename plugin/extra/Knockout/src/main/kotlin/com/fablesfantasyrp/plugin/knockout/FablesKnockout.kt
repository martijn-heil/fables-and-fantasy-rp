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
package com.fablesfantasyrp.plugin.knockout

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
import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import com.fablesfantasyrp.plugin.knockout.command.Commands
import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerDataEntityMapper
import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerDataEntityRepository
import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerEntity
import com.fablesfantasyrp.plugin.knockout.data.persistent.database.DatabasePersistentKnockoutPlayerDataRepository
import com.fablesfantasyrp.plugin.text.legacyText
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

internal val SYSPREFIX = legacyText(GLOBAL_SYSPREFIX)

internal lateinit var knockoutPlayerDataManager: EntityRepository<UUID, KnockoutPlayerEntity>

internal val PLUGIN: FablesKnockout
	get() = FablesKnockout.instance


class FablesKnockout : JavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		applyMigrations(this, "fables_knockout", this.classLoader)

		val knockoutPlayerDatEntityRepository = KnockoutPlayerDataEntityRepository(this,
				KnockoutPlayerDataEntityMapper(
						DatabasePersistentKnockoutPlayerDataRepository(this, fablesDatabase)
				)
		)
		knockoutPlayerDatEntityRepository.init()
		knockoutPlayerDataManager = knockoutPlayerDatEntityRepository


		val injector = Caturix.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val dispatcher = CommandGraph()
				.builder(builder)
				.commands()
				.registerMethods(Commands())
				.graph
				.dispatcher

		commands = dispatcher.commands.mapNotNull { command ->
			registerCommand(command.callable, this, command.allAliases.toList()) { this.launch(block = it) }
		}

		server.pluginManager.registerEvents(KnockoutListener(server), this)
	}

	override fun onDisable() {
		knockoutPlayerDataManager.saveAllDirty()
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesKnockout
	}
}
