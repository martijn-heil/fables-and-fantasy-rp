package com.fablesfantasyrp.plugin.characters.data.persistent.denizen

import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.characters.MutableDenizenPlayerCharacter
import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterDataRepository
import com.fablesfantasyrp.plugin.characters.data.simple.SimplePlayerCharacterData
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import java.util.*

private fun MutableDenizenPlayerCharacter.asSimple(): SimplePlayerCharacterData {
	return SimplePlayerCharacterData(
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

class DenizenPlayerCharacterRepository(private val server: Server) : PlayerCharacterDataRepository {
	private val idCache = HashMap<ULong, UUID>()

	override fun forOfflinePlayer(offlinePlayer: OfflinePlayer): Collection<PlayerCharacterData> {
		val characters = offlinePlayer.dFlags.getFlagValue("characters") as? MapTag ?: return emptyList()
		return characters.keys()
				.filter { it.matches(Regex("\\d+")) }
				.map { MutableDenizenPlayerCharacter(it.toULong(), server.getOfflinePlayer(offlinePlayer.uniqueId)).asSimple() }
	}

	override fun forId(id: ULong): PlayerCharacterData? {
		val player = idCache[id]?.let { Bukkit.getOfflinePlayer(it) }
		return if (player != null) {
			val result = MutableDenizenPlayerCharacter(id, player)
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

	override fun all(): Collection<PlayerCharacterData> {
		return server.offlinePlayers.asSequence()
				.map { this.forOfflinePlayer(it) }
				.flatten().toList()
	}

	override fun destroy(v: PlayerCharacterData) {
		TODO("Not yet implemented")
	}

	override fun create(v: PlayerCharacterData): PlayerCharacterData {
		TODO("Not yet implemented")
	}

	override fun update(v: PlayerCharacterData) {
		val mutablePlayerCharacter = MutableDenizenPlayerCharacter(v.id, v.player)
		mutablePlayerCharacter.name = v.name
		mutablePlayerCharacter.description = v.description
		mutablePlayerCharacter.age = v.age
	}
}
