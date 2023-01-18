package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizen.objects.ItemTag
import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.characters.data.CHARACTER_STATS_FLOOR
import com.fablesfantasyrp.plugin.characters.data.CharacterStats
import com.fablesfantasyrp.plugin.characters.data.Race
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.inventory.PassthroughInventory
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory
import com.fablesfantasyrp.plugin.inventory.inventory
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.FLATROOM
import com.fablesfantasyrp.plugin.utils.PLOTS
import com.fablesfantasyrp.plugin.utils.SPAWN
import com.fablesfantasyrp.plugin.utils.SerializableItemStack
import com.fablesfantasyrp.plugin.utilsoffline.location
import com.fablesfantasyrp.plugin.utilsoffline.offlineEnderChest
import com.fablesfantasyrp.plugin.utilsoffline.offlineInventory
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.sql.SQLIntegrityConstraintViolationException
import java.time.Instant

@Deprecated("Legacy Denizen")
val OfflinePlayer.denizenPlayerCharacters: List<MutableDenizenCharacter>
	get() {
		val characters = dFlags.getFlagValue("characters") as? MapTag ?: return emptyList()
		return characters.keys().mapNotNull {
			if (it.matches(Regex("\\d+"))) { // Yes, this was necessary
				MutableDenizenCharacter(it.toInt(), this)
			} else {
				FablesCharacters.instance.logger.severe("For player: $uniqueId (${name}) Ignoring corrupt player character data: $it")
				null
			}
		}
	}

@Deprecated("Legacy Denizen")
val OfflinePlayer.currentDenizenPlayerCharacter: MutableDenizenCharacter?
	get() {
		val currentCharacter = dFlags.getFlagValue("characters_current") ?: return null
		val id = currentCharacter.asElement().asInt()
		return MutableDenizenCharacter(id, this)
	}

@Suppress("DEPRECATION")
val Server.denizenPlayerCharacters: List<MutableDenizenCharacter>
	get() = offlinePlayers.asSequence().map { it.denizenPlayerCharacters }.flatten().toList()

private fun denizenInventoryMapToArray(map: MapTag, size: UInt): Array<ItemStack?> {
	val array = Array<ItemStack?>(size.toInt()) { null }

	map.map?.forEach {
		val index = it.key.str.toIntOrNull()?.minus(1) ?: return@forEach
		if (index.toUInt() >= size) return@forEach
		val item = (it.value as ItemTag).itemStack
		array[index] = item
	}

	return array
}

internal fun migrateDenizenToSql(plugin: Plugin,
								 characters: EntityCharacterRepository,
								 profiles: EntityProfileRepository) {
	val server = plugin.server
	val integrityViolations = ArrayList<MutableDenizenCharacter>()

	val chars = server.denizenPlayerCharacters.toMutableList()
	chars.sortBy { it.id }

	chars.forEach {
		try {
			var inventory: PassthroughPlayerInventory? = null
			var enderChest: PassthroughInventory? = null
			var location: Location? = null
			val player = it.player
			val currentDenizenCharacter = player.currentDenizenPlayerCharacter

			var useOfflinePlayerData = currentDenizenCharacter == it

			if (useOfflinePlayerData) {
				try {
					inventory = PassthroughPlayerInventory.copyOfBukkitInventory(player.offlineInventory)
					enderChest = PassthroughInventory.copyOfBukkitInventory(player.offlineEnderChest)
					location = player.location
				} catch (ex: Exception) {
					useOfflinePlayerData = false
				}
			}

			if (!useOfflinePlayerData) {
				inventory = (it.dataMap.getObject("inventory") as? MapTag)
						?.let { denizenInventoryMapToArray(it, PassthroughPlayerInventory.size.toUInt()) }
						?.map { if (it != null) SerializableItemStack(it) else null }
						?.toTypedArray()
						?.let { PassthroughPlayerInventory(it) } ?: PassthroughPlayerInventory.createEmpty()

				enderChest = (it.dataMap.getObject("enderchest") as? MapTag)
						?.let { denizenInventoryMapToArray(it, 27U) }
						?.map { if (it != null) SerializableItemStack(it) else null }
						?.toTypedArray()
						?.let { PassthroughInventory(it) } ?: PassthroughInventory(arrayOfNulls(27))
				location = if(arrayOf(PLOTS, FLATROOM).contains(it.location.world)) SPAWN else it.location
			}

			plugin.logger.info("Migrating #${it.id}")
			val profile = profiles.create(Profile(id = it.id, owner = player, description = it.name, isActive = true))
			profile.location = location!!
			profile.inventory.inventory.contents = inventory!!.contents
			profile.inventory.enderChest.contents = enderChest!!.contents

			val originalStats = it.stats
			val oldBoosters = when (it.race) {
				Race.HUMAN -> CharacterStats(strength = 1U, defense = 1U, agility = 1U)
				Race.HIGH_ELF -> CharacterStats(agility = 1U, intelligence = 2U)
				Race.DARK_ELF -> CharacterStats(strength = 2U, intelligence = 1U)
				Race.WOOD_ELF -> CharacterStats(agility = 3U)
				Race.DWARF -> CharacterStats(strength = 1U, defense = 2U)
				Race.TIEFLING -> CharacterStats(intelligence = 3U)
				Race.ORC -> CharacterStats(strength = 3U)
				Race.GOBLIN -> CharacterStats(defense = 1U, intelligence = 2U)
				Race.HALFLING -> CharacterStats(defense = 1U, agility = 1U, intelligence = 1U)
				else -> throw IllegalStateException() // These races didn't exist before
			}
			val stats = originalStats - oldBoosters - CHARACTER_STATS_FLOOR

			val character = characters.create(Character(
					id = profile.id,
					profile = profile,
					name = it.name,
					race = it.race,
					gender = it.gender,
					stats = stats,
					age = it.age,
					description = it.description,
					lastSeen = it.player.lastLogin.let { if (it != 0L) Instant.ofEpochMilli(it) else null },
					createdAt = null
			))
		} catch(e: SQLIntegrityConstraintViolationException) {
			integrityViolations.add(it)
			plugin.logger.warning("Error migrating character #${it.id}: ${e.message}")
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

	integrityViolations.forEach { Bukkit.getLogger().info(it.toString()) }
}
