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
