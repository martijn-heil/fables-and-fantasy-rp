package com.fablesfantasyrp.plugin.weights

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.plugin.weights.command.Commands
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
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module


internal val SYSPREFIX = "$GOLD${BOLD}[${AQUA}${BOLD} WEIGHTS ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesWeights.instance

class FablesWeights : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		saveDefaultConfig()

		val additive = HashMap<Material, Int>()
		val additiveSection = config.getConfigurationSection("additive")!!
		val additiveKeys = additiveSection.getKeys(false);
		for (key in additiveKeys) {
			val material = Material.valueOf(key.uppercase())
			val weight = additiveSection.getInt(key)
			additive[material] = weight
		}

		val singular = HashMap<Material, Int>()
		val singularSection = config.getConfigurationSection("singular")!!
		val singularKeys = singularSection.getKeys(false);
		for (key in singularKeys) {
			val material = Material.valueOf(key.uppercase())
			val weight = singularSection.getInt(key)
			singular[material] = weight
		}

		val weightsConfig = WeightsConfig(config.getInt("cap"), additive, singular)

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesWeights } binds(arrayOf(JavaPlugin::class))
			single { weightsConfig }
			singleOf(::WeightsChecker)
			singleOf(::Commands)
		}
		loadKoinModules(koinModule)
		get<WeightsChecker>().start()

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(get<Commands>())
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }
	}

	override fun onDisable() {
		get<WeightsChecker>().stop()
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesWeights
			private set
	}
}