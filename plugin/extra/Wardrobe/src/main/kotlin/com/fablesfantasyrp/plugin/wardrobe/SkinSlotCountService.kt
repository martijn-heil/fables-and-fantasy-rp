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

import com.fablesfantasyrp.plugin.characters.isStaffCharacter
import com.fablesfantasyrp.plugin.domain.EDEN
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import kotlinx.coroutines.withContext
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.Plugin

class SkinSlotCountService(private val plugin: Plugin,
						   private val vaultPermission: Permission) {
	suspend fun calculateSkinSlotCount(profile: Profile): Int {
		val player = profile.owner
		if (player == null || profile.isStaffCharacter || player.isOp) return 7

		return withContext(plugin.asyncDispatcher) {
			when {
				vaultPermission.playerInGroup(EDEN!!.name, player, "donator-heraldoflilith") -> 5
				vaultPermission.playerInGroup(EDEN!!.name, player, "donator-voidwalker") -> 4
				vaultPermission.playerInGroup(EDEN!!.name, player, "donator-elementalnavigator") -> 3
				vaultPermission.playerInGroup(EDEN!!.name, player, "donator-adventurer") -> 2
				vaultPermission.playerInGroup(EDEN!!.name, player, "donator-explorer") -> 1
				else -> 0
			}
		} + 2
	}
}
