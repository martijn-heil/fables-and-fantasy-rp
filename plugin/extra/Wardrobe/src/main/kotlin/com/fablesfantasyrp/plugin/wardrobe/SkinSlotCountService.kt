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
