package com.fablesfantasyrp.plugin.characters.nametags

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import me.neznamy.tab.api.TabAPI
import org.bukkit.entity.Player

class NameTagManager {
	private lateinit var tapi: TabAPI

	fun start() {
		tapi = TabAPI.getInstance()
		tapi.placeholderManager.registerRelationalPlaceholder(
				"%rel_characters_name_v2%", 1000) { tabObserver, tabTarget ->
			try {
				val observer = tabObserver.player as Player
				val target = tabTarget.player as Player
				val targetCharacter = target.currentPlayerCharacter
				val showCharacterNames = observer.dFlags.getFlagValue("characters_togglenames")
				  ?.let { it.asElement().asBoolean() } ?: true

				if (showCharacterNames && targetCharacter != null) {
					targetCharacter.name
				} else {
					target.name
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
				throw ex
			}
		}
	}
}
