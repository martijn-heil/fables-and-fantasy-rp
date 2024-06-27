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
package com.fablesfantasyrp.plugin.wardrobe

import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.utils.TransactionStep
import com.fablesfantasyrp.plugin.wardrobe.data.ProfileSkinRepository
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class WardrobeListener(private val profileSkins: ProfileSkinRepository,
					   private val skinService: SkinService) : Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	fun onPrePlayerSwitchProfile(e: PlayerSwitchProfileEvent) {
		val new = e.new
		val oldProfile = e.player.playerProfile

		e.transaction.steps.add(TransactionStep({
			if (new != null) {
				val skin = profileSkins.getLastUsed(new)?.skin
				skinService.setSkin(e.player, skin)
			} else {
				skinService.setSkin(e.player, null)
			}
		}, {
			e.player.playerProfile = oldProfile
		}))
	}
}
