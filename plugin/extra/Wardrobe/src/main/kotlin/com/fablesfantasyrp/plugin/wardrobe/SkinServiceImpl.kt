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

import com.fablesfantasyrp.plugin.wardrobe.data.Skin
import org.bukkit.Server
import org.bukkit.entity.Player

class SkinServiceImpl(private val server: Server,
					  private val originalPlayerProfileService: OriginalPlayerProfileService) : SkinService {
	override fun setSkin(player: Player, skin: Skin?) {
		if (skin != null) {
			val oldProfile = player.playerProfile
			val newProfile = server.createProfile(oldProfile.id, oldProfile.name)
			newProfile.setProperty(skin.toProfileProperty())
			player.playerProfile = newProfile
		} else {
			val original = originalPlayerProfileService.getOriginalPlayerProfile(player) ?: return
			player.playerProfile = original
		}
	}
}
