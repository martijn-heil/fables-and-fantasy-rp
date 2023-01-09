package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizen.objects.ItemTag
import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.inventory.PassthroughInventory
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory
import com.fablesfantasyrp.plugin.inventory.inventory
import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.utils.SerializableItemStack
import com.fablesfantasyrp.plugin.utilsoffline.location
import com.fablesfantasyrp.plugin.utilsoffline.offlineEnderChest
import com.fablesfantasyrp.plugin.utilsoffline.offlineInventory
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.inventory.ItemStack
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

internal fun migrateDenizenToSql(server: Server,
								 characters: EntityCharacterRepository,
								 playerInstances: EntityPlayerInstanceRepository) {
	val integrityViolations = ArrayList<MutableDenizenCharacter>()

	val chars = server.denizenPlayerCharacters.toMutableList()
	chars.sortBy { it.id }

	chars.forEach {
		try {
			val inventory: PassthroughPlayerInventory
			val enderChest: PassthroughInventory
			val player = it.player
			val currentDenizenCharacter = player.currentDenizenPlayerCharacter

			if (currentDenizenCharacter == it) {
				inventory = PassthroughPlayerInventory.copyOfBukkitInventory(player.offlineInventory)
				enderChest = PassthroughInventory.copyOfBukkitInventory(player.offlineEnderChest)
			} else {
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
			}

			val playerInstance = playerInstances.create(PlayerInstance(id = 0, owner = player, description = it.name, isActive = true))
			playerInstance.location = if (currentDenizenCharacter != it) it.location else player.location
			playerInstance.inventory.inventory.contents = inventory.contents
			playerInstance.inventory.enderChest.contents = enderChest.contents

			val character = characters.create(Character(
					id = playerInstance.id,
					playerInstance = playerInstance,
					name = it.name,
					race = it.race,
					gender = it.gender,
					stats = it.stats,
					age = it.age,
					description = it.description,
					lastSeen = Instant.ofEpochMilli(it.player.lastLogin),
					createdAt = null
			))

			// TODO money?
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

	integrityViolations.forEach { Bukkit.getLogger().info(it.toString()) }
}
