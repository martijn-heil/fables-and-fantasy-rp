package com.fablesfantasyrp.plugin.characters.data.persistent.denizen

import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.characters.MutableDenizenCharacter
import com.fablesfantasyrp.plugin.characters.data.CharacterData
import com.fablesfantasyrp.plugin.characters.data.CharacterDataRepository
import com.fablesfantasyrp.plugin.characters.data.simple.SimpleCharacterData
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import java.util.*

private fun MutableDenizenCharacter.asSimple(): SimpleCharacterData {
	return SimpleCharacterData(
			id = this.id,
			name = this.name,
			age = this.age,
			description = this.description,
			gender = this.gender,
			race = this.race,
			stats = this.stats,
			location = this.location,
			money = this.money,
			player = this.player
	)
}

class DenizenCharacterRepository(private val server: Server) : CharacterDataRepository {
	private val idCache = HashMap<ULong, UUID>()

	override fun forOfflinePlayer(offlinePlayer: OfflinePlayer): Collection<CharacterData> {
		val characters = offlinePlayer.dFlags.getFlagValue("characters") as? MapTag ?: return emptyList()
		return characters.keys()
				.filter { it.matches(Regex("\\d+")) }
				.map { MutableDenizenCharacter(it.toULong(), server.getOfflinePlayer(offlinePlayer.uniqueId)).asSimple() }
	}

	override fun forId(id: ULong): CharacterData? {
		val player = idCache[id]?.let { Bukkit.getOfflinePlayer(it) }
		return if (player != null) {
			val result = MutableDenizenCharacter(id, player)
			if (!result.isDeleted) {
				result.asSimple()
			} else {
				idCache.remove(id)
				null
			}
		} else {
			val result = this.all().find { it.id == id }
			if (result != null) idCache[id] = result.player.uniqueId
			result
		}
	}

	override fun allIds(): Collection<ULong> {
		return this.all().map { it.id }
	}

	override fun all(): Collection<CharacterData> {
		return server.offlinePlayers.asSequence()
				.map { this.forOfflinePlayer(it) }
				.flatten().toList()
	}

	override fun destroy(v: CharacterData) {
		TODO("Not yet implemented")
	}

	override fun create(v: CharacterData): CharacterData {
		TODO("Not yet implemented")
	}

	override fun update(v: CharacterData) {
		val mutablePlayerCharacter = MutableDenizenCharacter(v.id, v.player)
		mutablePlayerCharacter.name = v.name
		mutablePlayerCharacter.description = v.description
		mutablePlayerCharacter.age = v.age
	}
}
