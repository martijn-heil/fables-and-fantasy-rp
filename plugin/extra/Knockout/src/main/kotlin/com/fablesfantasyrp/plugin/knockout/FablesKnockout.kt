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
