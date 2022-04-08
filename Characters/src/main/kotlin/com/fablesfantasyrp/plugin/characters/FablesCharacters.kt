package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.characters.database.DatabasePlayerCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin

internal val SYSPREFIX = "[CHARACTERS]"

lateinit var databasePlayerCharacterRepository: DatabasePlayerCharacterRepository
	private set

class FablesCharacters : JavaPlugin() {

	override fun onEnable() {
		instance = this
		//playerCharacterRepository = PlayerCharacterRepository(this)
		//migrateDenizenToSql(server, playerCharacterRepository)
	}

	override fun onDisable() {
		//playerCharacterRepository.saveAllDirty()
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}

//fun PlayerCharacter.Companion.forId(id: ULong): PlayerCharacter = DatabasePlayerCharacter.forId(id)
//fun PlayerCharacter.Companion.all(): List<PlayerCharacter> = DatabasePlayerCharacter.all()
//fun PlayerCharacter.Companion.allForPlayer(p: OfflinePlayer): List<PlayerCharacter>

val OfflinePlayer.currentPlayerCharacter: PlayerCharacter
	get() {
		val id = dFlags.getFlagValue("characters_current").asElement().asLong().toULong()
		return DenizenPlayerCharacter(id, this)
	}

val OfflinePlayer.playerCharacters: List<PlayerCharacter>
	get() {
		val characters = dFlags.getFlagValue("characters") as? MapTag ?: return emptyList()
		return characters.keys().mapNotNull {
			if (it.matches(Regex("[0-9]+"))) { // Yes, this was necessary
				DenizenPlayerCharacter(it.toULong(), this)
			} else {
				//FablesCharacters.instance.logger.severe("For player: $uniqueId (${name}) Ignoring corrupt player character data: $it")
				null
			}
		}
	}

val Server.playerCharacters: List<PlayerCharacter>
	get() = offlinePlayers.asSequence().map { it.playerCharacters }.flatten().toList()
