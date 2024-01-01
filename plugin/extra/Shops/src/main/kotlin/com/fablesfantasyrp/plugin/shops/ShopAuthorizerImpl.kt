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
