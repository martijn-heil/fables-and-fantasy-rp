package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.command.Commands
import com.fablesfantasyrp.plugin.chat.command.provider.ChatModule
import com.fablesfantasyrp.plugin.chat.data.ChatPlayerRepository
import com.fablesfantasyrp.plugin.chat.data.entity.EntityChatPlayerRepository
import com.fablesfantasyrp.plugin.chat.data.entity.EntityChatPlayerRepositoryImpl
import com.fablesfantasyrp.plugin.chat.data.persistent.H2ChatPlayerRepository
import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
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
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

internal val SYSPREFIX = "${GOLD}[${DARK_AQUA}${BOLD} CHAT ${GOLD}]$GRAY"
val CHAT_CHAR = "$"

class FablesChat : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "fables_chat", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single <Plugin> { this@FablesChat } binds(arrayOf(JavaPlugin::class))
			singleOf(::ChatPreviewManager)
			singleOf(::ChatReceptionIndicatorManager)
			singleOf(::ChatListener)
			single {
				val tmp = EntityChatPlayerRepositoryImpl(get(), H2ChatPlayerRepository(server, fablesDatabase))
				tmp.init()
				tmp
			} binds(arrayOf(ChatPlayerRepository::class, EntityChatPlayerRepository::class))
			factoryOf(::ChatModule)
		}
		loadKoinModules(koinModule)

		get<ChatReceptionIndicatorManager>().start()
		get<ChatPreviewManager>().start()

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<ChatModule>())

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

		server.pluginManager.registerEvents(get<ChatListener>(), this)
		this.getCommand("ic")!!.setExecutor(Commands.CommandChatInCharacter())
		this.getCommand("looc")!!.setExecutor(Commands.CommandChatLocalOutOfCharacter())
		this.getCommand("ooc")!!.setExecutor(Commands.CommandChatOutOfCharacter())
		this.getCommand("dm")!!.setExecutor(Commands.CommandChatDirectMessage())
		this.getCommand("dm")!!.tabCompleter = Commands.CommandChatDirectMessage()
		this.getCommand("reply")!!.setExecutor(Commands.CommandReply())
		this.getCommand("staffchat")!!.setExecutor(Commands.CommandChatStaff())
		this.getCommand("spectatorchat")!!.setExecutor(Commands.CommandChatSpectator())
	}

	override fun onDisable() {
		get<EntityChatPlayerRepository>().saveAllDirty()
		commands.forEach { unregisterCommand(it) }
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesChat
	}
}
