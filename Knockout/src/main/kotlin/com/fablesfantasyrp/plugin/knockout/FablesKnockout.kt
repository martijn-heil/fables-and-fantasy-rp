package com.fablesfantasyrp.plugin.knockout

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import com.fablesfantasyrp.plugin.knockout.command.Commands
import com.fablesfantasyrp.plugin.knockout.data.entity.ChatPlayerDataEntityMapper
import com.fablesfantasyrp.plugin.knockout.data.entity.ChatPlayerDataEntityRepository
import com.fablesfantasyrp.plugin.knockout.data.entity.KnockoutPlayerEntity
import com.fablesfantasyrp.plugin.knockout.data.persistent.database.DatabasePersistentKnockoutPlayerDataRepository
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

internal val SYSPREFIX = "${GOLD}[${DARK_AQUA}${BOLD} KNOCKOUT ${GOLD}] $GRAY"

internal lateinit var knockoutPlayerDataManager: EntityRepository<UUID, KnockoutPlayerEntity>


class FablesKnockout : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "fables_chat", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		knockoutPlayerDataManager = ChatPlayerDataEntityRepository(this,
				ChatPlayerDataEntityMapper(
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

		dispatcher.commands.forEach { registerCommand(dispatcher, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(KnockoutListener(server), this)
	}

	override fun onDisable() {
		knockoutPlayerDataManager.saveAllDirty()
	}

	companion object {
		lateinit var instance: FablesKnockout
	}
}
