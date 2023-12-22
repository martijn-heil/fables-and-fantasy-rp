package com.fablesfantasyrp.plugin.halt

import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
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
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.GlobalContext

val SYSPREFIX = GLOBAL_SYSPREFIX

class FablesHalt : JavaPlugin(), KoinComponent {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(get<CharacterModule>())

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
		lateinit var instance: FablesHalt
	}
}

// player halted by halter
private val haltedMap = HashMap<Character, Character>()

var Character.haltedBy: Character?
	get() {
		return haltedMap[this]
	}
	private set(value) {
		if (value != null)
			haltedMap[this] = value
		else
			haltedMap.remove(this)
	}

fun Character.halt(halter: Character) {
	val profileManager = GlobalContext.get().get<ProfileManager>()
	val halterPlayer = profileManager.getCurrentForProfile(halter.profile)
	val thisPlayer = profileManager.getCurrentForProfile(this.profile)

	this.haltedBy = halter
	val halterCharName = halter.name
	val haltedCharName = this.name
	thisPlayer?.sendMessage("$SYSPREFIX ${RED}You have been halted by ${GRAY}${halterCharName}${RED}!")
	halterPlayer?.sendMessage("$SYSPREFIX ${GREEN}You have successfully halted ${GRAY}${haltedCharName}")
	Bukkit.broadcast("$SYSPREFIX $haltedCharName (${name}) has been " +
			"successfully halted by $halterCharName (${halter.name})", "fables.halt.notify")
}

fun Character.unhalt() {
	val profileManager = GlobalContext.get().get<ProfileManager>()
	val thisPlayer = profileManager.getCurrentForProfile(this.profile)
	this.haltedBy = null
	thisPlayer?.sendMessage("${GREEN}You are no longer halted.")
}
