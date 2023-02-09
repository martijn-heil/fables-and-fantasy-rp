package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.command.provider.ChatModule
import com.fablesfantasyrp.plugin.chat.data.entity.ChatPlayerDataEntityMapper
import com.fablesfantasyrp.plugin.chat.data.entity.ChatPlayerDataEntityRepository
import com.fablesfantasyrp.plugin.chat.data.entity.ChatPlayerEntity
import com.fablesfantasyrp.plugin.chat.data.persistent.database.DatabasePersistentChatPlayerDataRepository
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import com.fablesfantasyrp.plugin.utils.enforceDependencies
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
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

internal val SYSPREFIX = "${GOLD}[${DARK_AQUA}${BOLD} CHAT ${GOLD}]$GRAY"

internal lateinit var chatPreviewManager: ChatPreviewManager
internal lateinit var chatPlayerDataManager: EntityRepository<UUID, ChatPlayerEntity>

val CHAT_CHAR = "$"

class FablesChat : JavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "fables_chat", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		chatPreviewManager = ChatPreviewManager(this)
		val chatPlayerDatEntityRepository = ChatPlayerDataEntityRepository(this,
				ChatPlayerDataEntityMapper(
						DatabasePersistentChatPlayerDataRepository(server, fablesDatabase)
				)
		)
		chatPlayerDatEntityRepository.init()
		chatPlayerDataManager  = chatPlayerDatEntityRepository

		ChatReceptionIndicatorManager().start()

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(ChatModule(server))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		val commandsClass = Commands()
		rootDispatcherNode.registerMethods(commandsClass)
		val chatSpy = rootDispatcherNode.group("chatspy")
		val chatSpyClass = commandsClass.ChatSpy()
		chatSpy.registerMethods(chatSpyClass)
		chatSpy.group("excludes").registerMethods(chatSpyClass.Excludes())

		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(ChatListener(server), this)
		this.getCommand("ic")!!.setExecutor(Commands.CommandChatInCharacter())
		this.getCommand("looc")!!.setExecutor(Commands.CommandChatLocalOutOfCharacter())
		this.getCommand("ooc")!!.setExecutor(Commands.CommandChatOutOfCharacter())
		this.getCommand("staffchat")!!.setExecutor(Commands.CommandChatStaff())
		this.getCommand("spectatorchat")!!.setExecutor(Commands.CommandChatSpectator())
	}

	override fun onDisable() {
		chatPlayerDataManager.saveAllDirty()
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesChat
	}
}
