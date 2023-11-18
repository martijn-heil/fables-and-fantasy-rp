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
