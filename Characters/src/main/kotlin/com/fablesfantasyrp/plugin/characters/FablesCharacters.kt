package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin

class FablesCharacters : JavaPlugin() {

	override fun onEnable() {
		instance = this
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}
val OfflinePlayer.currentPlayerCharacter: PlayerCharacter
	get() {
		val id = dFlags.getFlagValue("characters_current").asElement().asInt().toUInt()
		return DenizenPlayerCharacter(id, this)
	}

val OfflinePlayer.playerCharacters: List<PlayerCharacter>
	get() {
		val characters = dFlags.getFlagValue("characters") as MapTag
		return characters.keys().map { DenizenPlayerCharacter(it.toUInt(), this) }
	}

val Server.playerCharacters: List<PlayerCharacter>
	get() = offlinePlayers.asSequence().map { it.playerCharacters }.flatten().toList()
