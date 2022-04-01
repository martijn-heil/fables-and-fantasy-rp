package com.fablesfantasyrp.plugin.halt

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

lateinit var instance: FablesHalt

class FablesHalt : JavaPlugin() {

	override fun onEnable() {
		instance = this

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
	this.sendMessage("${RED}You have been formally halted by ${GRAY}${halter.name}${RED}!")
	Bukkit.broadcast("${this.name} has been successfully halted by ${halter.name}", "fables.halt.notify")
}

fun Player.unhalt() {
	this.haltedBy = null
	this.sendMessage("${GREEN}You are no longer halted.")
}
