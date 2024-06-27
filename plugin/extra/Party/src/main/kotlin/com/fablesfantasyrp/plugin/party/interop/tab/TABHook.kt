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
package com.fablesfantasyrp.plugin.party.interop.tab

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.party.frunBlocking
import com.fablesfantasyrp.plugin.party.PartySpectatorManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import me.neznamy.tab.api.TabAPI
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class TABHook(private val profileManager: ProfileManager,
			  private val characters: CharacterRepository,
			  private val parties: PartyRepository,
			  private val spectatorManager: PartySpectatorManager) {
	private lateinit var tapi: TabAPI

	fun start() {
		tapi = TabAPI.getInstance()
		tapi.placeholderManager.registerRelationalPlaceholder(
			"%rel_party_icon_v2%", 1000) { tabObserver, tabTarget ->
			frunBlocking {
				try {
					val observer = tabObserver.player as Player
					val observerCharacter = profileManager.getCurrentForPlayer(observer)?.let { characters.forProfile(it) }
					val observerParty = observerCharacter?.let { parties.forMember(observerCharacter) }
						?: spectatorManager.getParty(observer)
						?: return@frunBlocking ""

					val target = tabTarget.player as Player
					val targetProfile = profileManager.getCurrentForPlayer(target)
					val targetCharacter = targetProfile?.let { characters.forProfile(it) }
						?: return@frunBlocking ""
					val targetParty = parties.forMember(targetCharacter)

					if (observerParty.members.contains(targetCharacter)) {
						"${ChatColor.GOLD}⭐"
					} else if (targetParty != null) {
						"${ChatColor.DARK_RED}\uD83D\uDDE1"
					} else {
						""
					}
				} catch (ex: Exception) {
					ex.printStackTrace()
					throw ex
				}
			}
		}
	}
}
