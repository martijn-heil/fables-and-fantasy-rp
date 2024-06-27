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

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.wardrobe.data.ProfileSkinRepository
import com.fablesfantasyrp.plugin.wardrobe.data.SkinRepository
import com.fablesfantasyrp.plugin.wardrobe.gui.WardrobeGui
import com.github.shynixn.mccoroutine.bukkit.launch
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import org.bukkit.plugin.java.JavaPlugin

class Commands(private val plugin: JavaPlugin,
			   private val profileManager: ProfileManager,
			   private val skins: SkinRepository,
			   private val profileSkins: ProfileSkinRepository,
			   private val skinService: SkinService,
			   private val slotCounter: SkinSlotCountService,
			   private val originalPlayerProfileService: OriginalPlayerProfileService) {

	@Command(aliases = ["wardrobe"], desc = "")
	@Require(Permission.Command.Wardrobe)
	fun wardrobe(@Sender sender: Profile) {
		val player = profileManager.getCurrentForProfile(sender)!!
		flaunch {
			val slotCount = slotCounter.calculateSkinSlotCount(sender)
			WardrobeGui(plugin, player, sender, slotCount, skins, profileSkins, skinService, originalPlayerProfileService)
				.show(player)
		}
	}
}
