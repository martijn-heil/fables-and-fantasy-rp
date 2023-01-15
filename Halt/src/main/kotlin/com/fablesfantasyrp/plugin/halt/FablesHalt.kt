package com.fablesfantasyrp.plugin.halt

import com.fablesfantasyrp.plugin.characters.characterRepository
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.playerinstance.command.provider.PlayerInstanceProvider
import com.fablesfantasyrp.plugin.playerinstance.playerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.playerInstances
import com.fablesfantasyrp.plugin.utils.enforceDependencies
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
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

val SYSPREFIX = "${DARK_RED}${BOLD}[${RED}${BOLD} HALT ${DARK_RED}${BOLD}]${GRAY}"

class FablesHalt : JavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(CharacterModule(server, characterRepository, PlayerInstanceProvider(playerInstances, playerInstanceManager)))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val dispatcher = CommandGraph()
				.builder(builder)
				.commands()
				.registerMethods(Commands())
				.graph()
				.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesHalt
	}
}

// player halted by halter
private val haltedMap = HashMap<Player, Player>()

var Player.haltedBy: Player?
	get() {
		return haltedMap[this]
	}
	private set(value) {
		if (value != null)
			haltedMap[this] = value
		else
			haltedMap.remove(this)
	}

fun Player.halt(halter: Player) {
	this.haltedBy = halter
	val halterCharName = halter.currentPlayerCharacter!!.name
	val haltedCharName = this.currentPlayerCharacter!!.name
	this.sendMessage("$SYSPREFIX ${RED}You have been halted by ${GRAY}${halterCharName}${RED}!")
	halter.sendMessage("$SYSPREFIX ${GREEN}You have successfully halted ${GRAY}${haltedCharName}")
	Bukkit.broadcast("$SYSPREFIX $haltedCharName (${name}) has been " +
			"successfully halted by $halterCharName (${halter.name})", "fables.halt.notify")
}

fun Player.unhalt() {
	this.haltedBy = null
	this.sendMessage("${GREEN}You are no longer halted.")
}
