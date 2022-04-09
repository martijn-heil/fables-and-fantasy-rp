package com.fablesfantasyrp.plugin.staffmode

import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import com.fablesfantasyrp.plugin.utils.ess
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.GameMode
import org.bukkit.plugin.java.JavaPlugin


val SYSPREFIX = "${DARK_RED}${BOLD}[${RED}${BOLD} STAFF MODE ${DARK_RED}${BOLD}]${GRAY}"

class FablesStaffMode : JavaPlugin() {

	override fun onEnable() {
		instance = this

		if(server.pluginManager.getPlugin("PlaceholderAPI") == null) {
			logger.severe("PlaceholderAPI not found, disabling plugin!")
			this.isEnabled = false
			return
		}

		StaffModePlaceholderExpansion().register();

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())

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
		lateinit var instance: FablesStaffMode
	}
}

// player halted by halter
private val onDuty = HashSet<FablesPlayer>()

var FablesPlayer.isOnDuty: Boolean
	get() = onDuty.contains(this)
	set(value) {
		if (value) {
			if(onDuty.add(this)) {
				player.sendMessage("$SYSPREFIX You are now on duty!")
				Bukkit.broadcast("$SYSPREFIX ${player.name} has gone on duty", "fables.staffmode.notify.duty")
			}
		} else {
			if (onDuty.remove(this)) {
				if (player.gameMode != GameMode.SURVIVAL) {
					player.gameMode = GameMode.SURVIVAL
					player.sendMessage("$SYSPREFIX Your gamemode was changed to survival because you are going off duty.")
				}

				val essPlayer = player.ess
				if (essPlayer.isGodModeEnabled) {
					essPlayer.isGodModeEnabled = false
					player.sendMessage("$SYSPREFIX Your god mode was disabled because you are going off duty.")
				}

				player.sendMessage("$SYSPREFIX You are now off duty!")
				Bukkit.broadcast("$SYSPREFIX ${player.name} has gone off duty", "fables.staffmode.notify.duty")
			}
		}
	}
