package com.fablesfantasyrp.plugin.rolls

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
import com.fablesfantasyrp.plugin.rolls.command.Commands
import com.fablesfantasyrp.plugin.utils.DISTANCE_TALK
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.FixedSuggestionsModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.gitlab.martijn_heil.nincommands.common.bukkit.unregisterCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


internal val ROLL_RANGE = DISTANCE_TALK

class FablesRolls : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(FixedSuggestionsModule(injector))
		injector.install(get<CharacterModule>())
		injector.install(get<ProfileModule>())

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val dispatcher = CommandGraph()
				.builder(builder)
				.commands()
				.registerMethods(Commands(get(), get(), get()))
				.graph()
				.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesRolls
	}
}