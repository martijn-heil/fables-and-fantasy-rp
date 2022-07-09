package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.command.provider.ChatModule
import com.fablesfantasyrp.plugin.chat.data.ChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.ChatPlayerDataEntityMapper
import com.fablesfantasyrp.plugin.chat.database.DatabasePersistentChatPlayerDataRepository
import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import com.fablesfantasyrp.plugin.database.entity.EntityRepositoryImpl
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

internal val SYSPREFIX = "${GOLD}[${DARK_AQUA}${BOLD} CHAT ${GOLD}] $GRAY"

internal lateinit var chatPreviewManager: ChatPreviewManager
internal lateinit var chatPlayerDataManager: EntityRepository<UUID, ChatPlayerData>

class FablesChat : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		chatPreviewManager = ChatPreviewManager(this)
		chatPlayerDataManager = EntityRepositoryImpl(
				ChatPlayerDataEntityMapper(
						DatabasePersistentChatPlayerDataRepository()
				)
		)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(ChatModule(server))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val dispatcher = CommandGraph()
				.builder(builder)
				.commands()
				.registerMethods(Commands())
				.graph()
				.dispatcher

		dispatcher.commands.forEach { registerCommand(dispatcher, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(ChatListener(server), this)
		this.getCommand("ic")!!.setExecutor(Commands.CommandChatInCharacter())
		this.getCommand("looc")!!.setExecutor(Commands.CommandChatLocalOutOfCharacter())
		this.getCommand("ooc")!!.setExecutor(Commands.CommandChatOutOfCharacter())
		this.getCommand("staffchat")!!.setExecutor(Commands.CommandChatStaff())
		this.getCommand("spectatorchat")!!.setExecutor(Commands.CommandChatSpectator())
	}

	companion object {
		lateinit var instance: FablesChat
	}
}
