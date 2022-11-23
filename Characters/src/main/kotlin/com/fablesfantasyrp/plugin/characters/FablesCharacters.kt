package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.command.Commands
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.characters.data.persistent.H2CharacterRepository
import com.fablesfantasyrp.plugin.database.FablesDatabase
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.playerinstance.playersInstances
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
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
import org.bukkit.ChatColor.*
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.command.Command

internal val SYSPREFIX = "$GOLD[ $GREEN${BOLD}CHARACTERS $GOLD] $GRAY"

lateinit var playerCharacterRepository: EntityCharacterRepository<*>
	private set

internal val PLUGIN get() = FablesCharacters.instance

class FablesCharacters : SuspendingJavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		//playerCharacterRepository = DenizenCharacterRepository(server)
		playerCharacterRepository = EntityCharacterRepository(
				H2CharacterRepository(server, FablesDatabase.fablesDatabase, playersInstances), playersInstances)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(CharacterModule(server))

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
		lateinit var instance: FablesCharacters
	}
}

val OfflinePlayer.currentPlayerCharacter: CharacterData?
	get() {
		val currentCharacter = dFlags.getFlagValue("characters_current") ?: return null
		val id = currentCharacter.asElement().asLong().toULong()
		return playerCharacterRepository.forId(id)!!
	}

val OfflinePlayer.playerCharacters: Collection<CharacterData>
	get() = playerCharacterRepository.forOwner(this)

val Server.playerCharacters: Collection<CharacterData>
	get() = playerCharacterRepository.all()
