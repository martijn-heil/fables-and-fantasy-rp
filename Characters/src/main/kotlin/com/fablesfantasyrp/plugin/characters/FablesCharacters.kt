package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.command.Commands
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepositoryImpl
import com.fablesfantasyrp.plugin.characters.data.persistent.H2CharacterRepository
import com.fablesfantasyrp.plugin.database.FablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.playerinstance.command.provider.PlayerInstanceProvider
import com.fablesfantasyrp.plugin.playerinstance.playerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.playerInstances
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
import org.bukkit.entity.Player

internal val SYSPREFIX = "$GOLD[ $GREEN${BOLD}CHARACTERS $GOLD] $GRAY"

lateinit var characterRepository: EntityCharacterRepository
	private set

internal val PLUGIN get() = FablesCharacters.instance

class FablesCharacters : SuspendingJavaPlugin() {
	private lateinit var commands: Collection<Command>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_CHARACTERS", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		//playerCharacterRepository = DenizenCharacterRepository(server)
		val characterRepositoryImpl = EntityCharacterRepositoryImpl(
				H2CharacterRepository(server, FablesDatabase.fablesDatabase, playerInstances), playerInstances)
		characterRepositoryImpl.init()
		characterRepository = characterRepositoryImpl

		migrateDenizenToSql(server, characterRepository, playerInstances)

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(CharacterModule(server, characterRepository, PlayerInstanceProvider(playerInstances)))

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		val charactersCommand = rootDispatcherNode.group("characters", "chars", "fchars", "fcharacters")
		charactersCommand.registerMethods(Commands.Characters(this, playerInstances))
		charactersCommand.group("stats").registerMethods(Commands.Characters.Stats(this))
		rootDispatcherNode.registerMethods(Commands(this))
		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(CharactersListener(characterRepository), this)
		server.pluginManager.registerEvents(CharactersLiveMigrationListener(characterRepository), this)
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}

var Player.currentPlayerCharacter: Character?
	get() {
		return playerInstanceManager.getCurrentForPlayer(this)
				?.let { characterRepository.forId(it.id.toULong()) }
	}
	set(value) {
		require(value != null)
		playerInstanceManager.setCurrentForPlayer(this, playerInstances.forId(value.id.toInt())!!)
	}

val OfflinePlayer.playerCharacters: Collection<Character>
	get() = characterRepository.forOwner(this)

val Server.playerCharacters: Collection<Character>
	get() = characterRepository.all()
