package com.fablesfantasyrp.plugin.knockout

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import com.fablesfantasyrp.plugin.knockout.command.Commands
import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerDataEntityMapper
import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerDataEntityRepository
import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerEntity
import com.fablesfantasyrp.plugin.knockout.data.persistent.database.DatabasePersistentKnockoutPlayerDataRepository
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import java.util.*

internal val SYSPREFIX =
		miniMessage.deserialize("<gold>[</gold> <yellow><bold>KNOCKOUT</bold></yellow> <gold>]</gold> ")

internal lateinit var knockoutPlayerDataManager: EntityRepository<UUID, KnockoutPlayerEntity>

internal val PLUGIN: FablesKnockout
	get() = FablesKnockout.instance


class FablesKnockout : SuspendingJavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		applyMigrations(this, "fables_knockout", this.classLoader)

		knockoutPlayerDataManager = KnockoutPlayerDataEntityRepository(this,
				KnockoutPlayerDataEntityMapper(
						DatabasePersistentKnockoutPlayerDataRepository(server, fablesDatabase)
				)
		).init()


		val injector = Intake.createInjector()
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
				.graph()
				.dispatcher

		dispatcher.commands.forEach { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(KnockoutListener(server), this)
	}

	override fun onDisable() {
		knockoutPlayerDataManager.saveAllDirty()
	}

	companion object {
		lateinit var instance: FablesKnockout
	}
}
