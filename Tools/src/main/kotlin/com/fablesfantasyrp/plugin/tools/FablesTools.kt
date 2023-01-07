package com.fablesfantasyrp.plugin.tools

import com.fablesfantasyrp.plugin.characters.characterRepository
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.inventory.FablesInventoryPlugin
import com.fablesfantasyrp.plugin.playerinstance.command.provider.PlayerInstanceModule
import com.fablesfantasyrp.plugin.playerinstance.command.provider.PlayerInstanceProvider
import com.fablesfantasyrp.plugin.playerinstance.playerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.playerInstances
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderProvider
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.gitlab.martijn_heil.nincommands.common.bukkit.unregisterCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.entity.Player

internal val SYSPREFIX = "${GOLD}${BOLD}[${LIGHT_PURPLE}${BOLD} TOOLS ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesTools.instance

class FablesTools : SuspendingJavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(PlayerInstanceModule(playerInstances, playerInstanceManager, BukkitSenderProvider(Player::class.java)))
		injector.install(CharacterModule(server, characterRepository, PlayerInstanceProvider(playerInstances)))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(Commands())
		rootDispatcherNode.registerMethods(InventoryCommands(FablesInventoryPlugin.instance.mirroredInventoryManager))
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}


	companion object {
		lateinit var instance: FablesTools
	}
}
