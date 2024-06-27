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
package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.regions.RegionContainer
import org.bukkit.Server
import org.bukkit.entity.Player

class ShopAuthorizerImpl(private val server: Server,
						 private val profileManager: ProfileManager,
						 private val regionContainer: RegionContainer) : ShopAuthorizer {
	override suspend fun mayEdit(shop: Shop, who: Profile): Boolean {
		val player = profileManager.getCurrentForProfile(who)
		return shop.owner == who || player?.hasPermission(Permission.Admin) == true
	}

	override suspend fun mayManagePublicShops(who: Player): Boolean {
		return who.hasPermission(Permission.Admin)
	}

	override fun mayCreateShopAt(who: Player, location: BlockIdentifier): Boolean {
		val canBuild = regionContainer.createQuery().testBuild(
			BukkitAdapter.adapt(location.toLocation()),
			(server.pluginManager.getPlugin("WorldGuard") as WorldGuardPlugin).wrapPlayer(who)
		)

		return canBuild
	}
}
