package com.fablesfantasyrp.plugin.targeting

import com.fablesfantasyrp.plugin.glowing.FablesGlowing
import com.fablesfantasyrp.plugin.targeting.data.MemorySimpleTargetingPlayerDataRepository
import com.fablesfantasyrp.plugin.targeting.data.SimpleTargetingPlayerDataRepository
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.caturix.spigot.common.CommonModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.BukkitAuthorizer
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.BukkitModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderModule
import com.fablesfantasyrp.caturix.spigot.common.bukkit.registerCommand
import com.fablesfantasyrp.caturix.spigot.common.bukkit.unregisterCommand
import com.fablesfantasyrp.caturix.Caturix
import com.fablesfantasyrp.caturix.fluent.CommandGraph
import com.fablesfantasyrp.caturix.parametric.ParametricBuilder
import com.fablesfantasyrp.caturix.parametric.provider.PrimitivesModule
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin


val SYSPREFIX = GLOBAL_SYSPREFIX

lateinit var targetingPlayerDataRepository: SimpleTargetingPlayerDataRepository
	private set

class FablesTargeting : JavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		targetingPlayerDataRepository = MemorySimpleTargetingPlayerDataRepository(FablesGlowing.instance.glowingManager)

		val injector = Caturix.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("target", "ftarget", "ta").registerMethods(Commands.Target(targetingPlayerDataRepository))
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }
		server.pluginManager.registerEvents(TargetingListener(), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesTargeting
	}
}
