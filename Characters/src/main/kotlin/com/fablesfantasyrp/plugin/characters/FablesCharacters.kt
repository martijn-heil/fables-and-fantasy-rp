package com.fablesfantasyrp.plugin.characters

import com.denizenscript.denizencore.objects.core.MapTag
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class FablesCharacters : JavaPlugin() {

	override fun onEnable() {
		instance = this
		server.scheduler.scheduleSyncRepeatingTask(this, {
			server.onlinePlayers.forEach {
				logger.info(it.currentCharacter.toString())
			}
		}, 0, 200)
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}
val Player.currentCharacter: Character
	get() {
		val id = dFlags.getFlagValue("characters_current").asElement().asInt().toUInt()
		return DenizenCharacter(id, this)
	}

val Player.characters: List<Character>
	get() {
		val characters = dFlags.getFlagValue("characters") as MapTag
		return characters.keys().map { DenizenCharacter(it.toUInt(), this) }
	}
