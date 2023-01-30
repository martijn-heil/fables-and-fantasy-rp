package com.fablesfantasyrp.plugin.distances

import com.fablesfantasyrp.plugin.characters.characterRepository
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.distances.command.Commands
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileProvider
import com.fablesfantasyrp.plugin.profile.profileManager
import com.fablesfantasyrp.plugin.profile.profiles
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
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.plugin.java.JavaPlugin

internal val SYSPREFIX = "${ChatColor.GOLD}[${ChatColor.DARK_AQUA}${ChatColor.BOLD} DISTANCES ${ChatColor.GOLD}] ${ChatColor.GRAY}"

class FablesDistances : JavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		instance = this

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(FixedSuggestionsModule(injector))
		injector.install(CharacterModule(server, characterRepository, ProfileProvider(profiles, profileManager)))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val dispatcher = CommandGraph()
				.builder(builder)
				.commands()
				.registerMethods(Commands(this))
				.graph()
				.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesDistances
	}
}
