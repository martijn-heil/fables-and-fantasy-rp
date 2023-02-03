package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.command.Commands
import com.fablesfantasyrp.plugin.characters.command.LegacyCommands
import com.fablesfantasyrp.plugin.characters.command.provider.CharacterModule
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepositoryImpl
import com.fablesfantasyrp.plugin.characters.data.persistent.H2CharacterRepository
import com.fablesfantasyrp.plugin.database.FablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.profile.ProfilePrompter
import com.fablesfantasyrp.plugin.profile.profileManager
import com.fablesfantasyrp.plugin.profile.profiles
import com.fablesfantasyrp.plugin.utils.Services
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
import org.bukkit.plugin.ServicePriority

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
		val h2CharacterRepository = H2CharacterRepository(server, FablesDatabase.fablesDatabase, profiles)
		val characterRepositoryImpl = EntityCharacterRepositoryImpl(h2CharacterRepository, profiles)
		characterRepositoryImpl.init()
		characterRepository = characterRepositoryImpl
		server.servicesManager.register(EntityCharacterRepository::class.java, characterRepository, this, ServicePriority.Normal)

		val characterModule = CharacterModule()

		val injector = Intake.createInjector()
		injector.install(PrimitivesModule())
		injector.install(BukkitModule(server))
		injector.install(BukkitSenderModule())
		injector.install(CommonModule())
		injector.install(characterModule)

		val builder = ParametricBuilder(injector)
		builder.authorizer = BukkitAuthorizer()

		val prompter = Services.get<ProfilePrompter>()

		val rootDispatcherNode = CommandGraph().builder(builder).commands()
		rootDispatcherNode.registerMethods(Commands(this))

		val charactersCommand = rootDispatcherNode.group("character", "char", "fchar", "fcharacter")
		val characterCommands = Commands.Characters(this, profiles, profileManager, prompter)
		charactersCommand.registerMethods(characterCommands)
		rootDispatcherNode.registerMethods(LegacyCommands(characterCommands))

		charactersCommand.group("stats").registerMethods(Commands.Characters.Stats(this))
		charactersCommand.group("change").registerMethods(Commands.Characters.Change(this, Services.get()))

		val dispatcher = rootDispatcherNode.dispatcher

		commands = dispatcher.commands.mapNotNull { registerCommand(it.callable, this, it.allAliases.toList()) }

		server.pluginManager.registerEvents(CharactersListener(this, characterRepository, profiles, profileManager, Services.get()), this)
		server.pluginManager.registerEvents(CharactersLiveMigrationListener(this, characterRepository), this)
		server.pluginManager.registerEvents(CharacterCreationListener(), this)

		if (server.pluginManager.isPluginEnabled("TAB") && server.pluginManager.isPluginEnabled("Denizen") ) {
			com.fablesfantasyrp.plugin.characters.nametags.NameTagManager().start()
		}
	}

	override fun onDisable() {
		commands.forEach { unregisterCommand(it) }
		characterRepository.saveAllDirty()
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}

var Player.currentPlayerCharacter: Character?
	get() {
		return profileManager.getCurrentForPlayer(this)
				?.let { characterRepository.forId(it.id) }
	}
	set(value) {
		require(value != null)
		profileManager.setCurrentForPlayer(this, profiles.forId(value.id.toInt())!!)
	}

val OfflinePlayer.playerCharacters: Collection<Character>
	get() = characterRepository.forOwner(this)

val Server.playerCharacters: Collection<Character>
	get() = characterRepository.all()
