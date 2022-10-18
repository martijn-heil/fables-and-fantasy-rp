package com.fablesfantasyrp.plugin.targeting

import com.fablesfantasyrp.plugin.targeting.data.MemorySimpleTargetingPlayerDataRepository
import com.fablesfantasyrp.plugin.targeting.data.SimpleTargetingPlayerDataRepository
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


val SYSPREFIX = "${DARK_RED}${BOLD}[${RED}${BOLD} TARGETING ${DARK_RED}${BOLD}]${GRAY}"

lateinit var targetingPlayerDataRepository: SimpleTargetingPlayerDataRepository
	private set

class FablesTargeting : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		val glowingManager = DenizenGlowingManager()
		targetingPlayerDataRepository = MemorySimpleTargetingPlayerDataRepository(glowingManager)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.group("target").registerMethods(Commands.Target(targetingPlayerDataRepository))
		val dispatcher = rootDispatcherNode.dispatcher

		registerCommand(dispatcher, this, dispatcher.aliases.toList())
		server.pluginManager.registerEvents(TargetingListener(), this)
	}

	companion object {
		lateinit var instance: FablesTargeting
	}
}
