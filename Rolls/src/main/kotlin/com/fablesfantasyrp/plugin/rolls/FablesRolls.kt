package com.fablesfantasyrp.plugin.rolls

import com.fablesfantasyrp.plugin.characters.command.provider.PlayerCharacterModule
import com.fablesfantasyrp.plugin.rolls.command.Commands
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.FixedSuggestionsModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.plugin.java.JavaPlugin


internal val ROLL_RANGE = 15U

class FablesRolls : JavaPlugin() {

	override fun onEnable() {
		instance = this

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(FixedSuggestionsModule(injector))
		injector.install(PlayerCharacterModule(server))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val dispatcher = CommandGraph()
				.builder(builder)
				.commands()
				.registerMethods(Commands())
				.graph()
				.dispatcher

		registerCommand(dispatcher, this, dispatcher.aliases.toList())
	}

	companion object {
		lateinit var instance: FablesRolls
	}
}
