package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.characters.database.DatabasePlayerCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLIntegrityConstraintViolationException

internal val SYSPREFIX = "[CHARACTERS]"

lateinit var databasePlayerCharacterRepository: DatabasePlayerCharacterRepository
	private set

class FablesCharacters : JavaPlugin() {

	override fun onEnable() {
		instance = this
		//playerCharacterRepository = PlayerCharacterRepository(server)
		//migrateDenizenToSql(server, playerCharacterRepository)
	}

	override fun onDisable() {
		//playerCharacterRepository.saveAllDirty()
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}
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
				FablesCharacters.instance.logger.severe("For player: $uniqueId (${name}) Ignoring corrupt player character data: $it")
				null
			}
		}
	}

val Server.playerCharacters: List<PlayerCharacter>
	get() = offlinePlayers.asSequence().map { it.playerCharacters }.flatten().toList()

private fun migrateDenizenToSql(server: Server, playerCharacterRepo: DatabasePlayerCharacterRepository) {
	val integrityViolations = ArrayList<PlayerCharacter>()

	val chars = server.offlinePlayers.asSequence().map { it.playerCharacters }.flatten().toMutableList()
	chars.sortBy { it.id }

	chars.forEach {
		try {
			playerCharacterRepo.create(it.name, it.age, it.description, it.gender,
					it.race, it.stats, it.location, it.money, it.player)
		} catch(e: SQLIntegrityConstraintViolationException) {
			integrityViolations.add(it)
			e.printStackTrace()
		} catch(e: java.lang.IllegalArgumentException) {
			if (e.message?.startsWith("No enum constant com.fablesfantasyrp.plugin.characters.Race.") == true) {
				FablesCharacters.instance.logger.severe(
						"Ignoring legacy character: id = ${it.id}, " +
								"player = ${it.player.uniqueId} (${it.player.name}), " +
								"name = ${it.name}, gender = ${it.gender}")
			} else {
				throw e
			}
		}
	}
	playerCharacterRepo.saveAllDirty()

	integrityViolations.forEach { Bukkit.getLogger().info(it.toString()) }
}
