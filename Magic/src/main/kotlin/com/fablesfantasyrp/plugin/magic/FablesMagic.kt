package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.command.provider.PlayerCharacterModule
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

val SYSPREFIX = "${DARK_PURPLE}${BOLD}[${AQUA}${BOLD}MAGIC${DARK_PURPLE}${BOLD}]${GRAY}"

class FablesMagic : JavaPlugin() {

	override fun onEnable() {
		instance = this

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
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
		lateinit var instance: FablesMagic
	}
}
