package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.characters.playerCharacterRepository
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin

lateinit var fablesPlayerRepository: FablesPlayerRepository
	private set

class FablesPlayerData : JavaPlugin() {
	override fun onEnable() {
		instance = this
		fablesPlayerRepository = FablesPlayerRepository(this)
	}

	override fun onDisable() {
		fablesPlayerRepository.saveAllDirty()
	}

	companion object {
		lateinit var instance: FablesPlayerData
	}
}

var OfflinePlayer.currentPlayerCharacter
	get() = fablesPlayerRepository.forPlayer(this).currentCharacter
	set(value) { fablesPlayerRepository.forPlayer(this).currentCharacter = value }

val OfflinePlayer.playerCharacters
	get() = playerCharacterRepository.allForPlayer(this)

var OfflinePlayer.currentChatChannel
	get() = fablesPlayerRepository.forPlayer(this).chatChannel
	set(value) { fablesPlayerRepository.forPlayer(this).chatChannel = value }
