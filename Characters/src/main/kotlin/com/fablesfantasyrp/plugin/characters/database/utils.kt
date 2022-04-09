package com.fablesfantasyrp.plugin.characters.database

import com.fablesfantasyrp.plugin.characters.PlayerCharacter
import com.fablesfantasyrp.plugin.playerdata.FablesOfflinePlayer

var FablesOfflinePlayer.currentPlayerCharacter: PlayerCharacter?
	get() = if (rawData.currentCharacterId != null) DatabasePlayerCharacter.forId(rawData.currentCharacterId!!) else null
	set(value) {
		// TODO more behaviour
		rawData.currentCharacterId = value?.id
	}

val FablesOfflinePlayer.playerCharacters: List<PlayerCharacter>
	get() = DatabasePlayerCharacter.allForPlayer(offlinePlayer)
