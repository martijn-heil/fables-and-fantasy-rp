package com.fablesfantasyrp.plugin.characters.nametags

import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.EDEN
import me.neznamy.tab.api.TabAPI
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

class NameTagManager(private val profileManager: ProfileManager,
					 private val characters: EntityCharacterRepository) {
	private lateinit var tapi: TabAPI
	private val ROBIN = UUID.fromString("6d147f5e-2509-4f92-82c6-86b57058e916")

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
				if (target.uniqueId == ROBIN && target.gameMode == GameMode.CREATIVE &&
						target.isFlying && target.flySpeed >= 0.3 && target.location.world == EDEN) {
					"'Fighter Jet' Robin"
				} else if (showCharacterNames && targetCharacter != null) {
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
