package com.fablesfantasyrp.plugin.staffmode

import com.fablesfantasyrp.plugin.staffmode.event.PlayerSwitchStaffDutyModeEvent
import com.fablesfantasyrp.plugin.utils.ToggleableState
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
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


val SYSPREFIX = "${DARK_RED}${BOLD}[${RED}${BOLD} STAFF MODE ${DARK_RED}${BOLD}]${GRAY}"

internal var moreLoggingHook: MoreLoggingHook? = null
	private set

class FablesStaffMode : JavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		instance = this

		if(server.pluginManager.getPlugin("PlaceholderAPI") == null) {
			logger.severe("PlaceholderAPI not found, disabling plugin!")
			this.isEnabled = false
			return
		}

		if (server.pluginManager.getPlugin("FablesMoreLogging") != null) {
			moreLoggingHook = MoreLoggingHook()
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

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		// This is the only reliable way I've managed to solve this problem
		server.scheduler.scheduleSyncRepeatingTask(this, {
			server.onlinePlayers.forEach { it.updateCommands() }
		},0, 10)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesStaffMode
	}
}

// player halted by halter
private val onDuty = HashSet<Player>()

var Player.isOnDuty: Boolean
	get() = onDuty.contains(this)
	set(value) {
		val onOff = if (value) "on" else "off"
		if (value) {
			if (onDuty.add(this)) {
				this.sendMessage("$SYSPREFIX You are now on duty!")
				Bukkit.broadcast("$SYSPREFIX ${this.name} has gone on duty", "fables.staffmode.notify.duty")
				moreLoggingHook?.logDutySwitch(this, ToggleableState.fromIsActiveBoolean(value))
				Bukkit.getPluginManager().callEvent(PlayerSwitchStaffDutyModeEvent(this, true))
			} else {
				this.sendMessage("$SYSPREFIX You are already $onOff duty!")
			}
		} else {
			if (onDuty.remove(this)) {
				if (this.gameMode != GameMode.SURVIVAL) {
					this.gameMode = GameMode.SURVIVAL
					this.sendMessage("$SYSPREFIX Your game mode was changed to survival because you are going off duty.")
				}

				if (this.isInvulnerable) {
					this.isInvulnerable = false
					this.sendMessage("$SYSPREFIX Your god mode was disabled because you are going off duty.")
				}

				Bukkit.getPluginManager().callEvent(PlayerSwitchStaffDutyModeEvent(this, false))
				this.sendMessage("$SYSPREFIX You are now off duty!")
				Bukkit.broadcast("$SYSPREFIX ${this.name} has gone off duty", "fables.staffmode.notify.duty")
				moreLoggingHook?.logDutySwitch(this, ToggleableState.fromIsActiveBoolean(value))
			} else {
				this.sendMessage("$SYSPREFIX You are already $onOff duty!")
			}
		}
	}
