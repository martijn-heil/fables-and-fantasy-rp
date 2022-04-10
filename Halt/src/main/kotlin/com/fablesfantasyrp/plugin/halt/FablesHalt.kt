package com.fablesfantasyrp.plugin.halt

import com.fablesfantasyrp.plugin.characters.command.provider.PlayerCharacterModule
import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
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
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

val SYSPREFIX = "${DARK_RED}${BOLD}[${RED}${BOLD} HALT ${DARK_RED}${BOLD}]${GRAY}"

class FablesHalt : JavaPlugin() {

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
