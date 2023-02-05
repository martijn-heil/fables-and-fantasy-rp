package com.fablesfantasyrp.plugin.characters.nametags

import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.profile.ProfileManager
import me.neznamy.tab.api.TabAPI
import org.bukkit.entity.Player

class NameTagManager(private val profileManager: ProfileManager,
					 private val characters: EntityCharacterRepository) {
	private lateinit var tapi: TabAPI

	fun start() {
		tapi = TabAPI.getInstance()
		tapi.placeholderManager.registerRelationalPlaceholder(
				"%rel_characters_name_v2%", 1000) { tabObserver, tabTarget ->
			try {
				val observer = tabObserver.player as Player
				val target = tabTarget.player as Player
				val targetProfile = profileManager.getCurrentForPlayer(target)
				val targetCharacter = targetProfile?.let { characters.forProfile(it) }
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
