/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.characters.nametags

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.frunBlocking
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.domain.EDEN
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isVanished
import me.neznamy.tab.api.TabAPI
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

class NameTagManager(private val profileManager: ProfileManager,
					 private val characters: CharacterRepository) {
	private lateinit var tapi: TabAPI
	private val ROBIN = UUID.fromString("6d147f5e-2509-4f92-82c6-86b57058e916")

	fun start() {
		tapi = TabAPI.getInstance()
		tapi.placeholderManager.registerRelationalPlaceholder(
			"%rel_characters_name_v2%", 1000) { tabObserver, tabTarget ->
			frunBlocking {
				try {
					val observer = tabObserver.player as Player
					val target = tabTarget.player as Player
					val targetProfile = profileManager.getCurrentForPlayer(target)
					val targetCharacter = targetProfile?.let { characters.forProfile(it) }
					val showCharacterNames = observer.dFlags.getFlagValue("characters_togglenames")
						?.let { it.asElement().asBoolean() } ?: true
					if (target.uniqueId == ROBIN && target.gameMode == GameMode.CREATIVE &&
						target.isFlying && target.flySpeed >= 0.3 && !target.isVanished &&
						target.location.world == EDEN && targetProfile != null) {
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
}
