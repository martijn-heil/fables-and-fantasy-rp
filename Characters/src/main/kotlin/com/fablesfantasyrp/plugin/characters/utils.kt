package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.database.DatabasePlayerCharacterRepository
import org.bukkit.Bukkit
import org.bukkit.Server
import java.sql.SQLIntegrityConstraintViolationException

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
