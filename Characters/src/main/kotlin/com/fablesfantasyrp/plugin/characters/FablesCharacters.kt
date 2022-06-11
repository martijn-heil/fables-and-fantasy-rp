package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.characters.command.Commands
import com.fablesfantasyrp.plugin.characters.command.provider.PlayerCharacterModule
import com.fablesfantasyrp.plugin.characters.database.DatabasePlayerCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.playerdata.FablesOfflinePlayer
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import com.gitlab.martijn_heil.nincommands.common.CommonModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.BukkitAuthorizer
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.BukkitModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.provider.sender.BukkitSenderModule
import com.gitlab.martijn_heil.nincommands.common.bukkit.registerCommand
import com.sk89q.intake.Intake
import com.sk89q.intake.fluent.CommandGraph
import com.sk89q.intake.parametric.ParametricBuilder
import com.sk89q.intake.parametric.provider.PrimitivesModule
import org.bukkit.ChatColor.*
import org.bukkit.OfflinePlayer
import org.bukkit.Server

internal val SYSPREFIX = "$GOLD[ $GREEN${BOLD}CHARACTERS $GOLD] $GRAY"

lateinit var playerCharacterRepository: DatabasePlayerCharacterRepository
	private set

internal val PLUGIN get() = FablesCharacters.instance

class FablesCharacters : SuspendingJavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
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
				.registerMethods(Commands(this))
				.graph()
				.dispatcher

		dispatcher.commands.forEach { registerCommand(dispatcher, this, it.allAliases.toList()) }
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}

//fun PlayerCharacter.Companion.forId(id: ULong): PlayerCharacter = DatabasePlayerCharacter.forId(id)
//fun PlayerCharacter.Companion.all(): List<PlayerCharacter> = DatabasePlayerCharacter.all()
//fun PlayerCharacter.Companion.allForPlayer(p: OfflinePlayer): List<PlayerCharacter>

@Deprecated("Use FablesOfflinePlayer#currentPlayerCharacter")
val OfflinePlayer.currentPlayerCharacter: PlayerCharacter?
	get() {
		val currentCharacter = dFlags.getFlagValue("characters_current") ?: return null
		val id = currentCharacter.asElement().asLong().toULong()
		return DenizenPlayerCharacter(id, this)
	}

@Deprecated("Use FablesOfflinePlayer#playerCharacters")
val OfflinePlayer.playerCharacters: List<PlayerCharacter>
	get() {
		val characters = dFlags.getFlagValue("characters") as? MapTag ?: return emptyList()
		return characters.keys().mapNotNull {
			if (it.matches(Regex("\\d+"))) { // Yes, this was necessary
				DenizenPlayerCharacter(it.toULong(), this)
			} else {
				//FablesCharacters.instance.logger.severe("For player: $uniqueId (${name}) Ignoring corrupt player character data: $it")
				null
			}
		}
	}

val Server.playerCharacters: List<PlayerCharacter>
	get() = offlinePlayers.asSequence().map { it.playerCharacters }.flatten().toList()

@Suppress("DEPRECATION")
val FablesOfflinePlayer.currentPlayerCharacter: PlayerCharacter?
	get() = offlinePlayer.currentPlayerCharacter


@Suppress("DEPRECATION")
val FablesOfflinePlayer.playerCharacters: List<PlayerCharacter>
	get() = offlinePlayer.playerCharacters
