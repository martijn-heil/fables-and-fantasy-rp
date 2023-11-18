package com.fablesfantasyrp.plugin.wardrobe

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.wardrobe.data.ProfileSkinRepository
import com.fablesfantasyrp.plugin.wardrobe.data.SkinRepository
import com.fablesfantasyrp.plugin.wardrobe.gui.WardrobeGui
import com.github.shynixn.mccoroutine.bukkit.launch
import com.gitlab.martijn_heil.nincommands.common.Sender
import com.sk89q.intake.Command
import com.sk89q.intake.Require
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
		plugin.launch {
			val slotCount = slotCounter.calculateSkinSlotCount(sender)
			WardrobeGui(plugin, player, sender, slotCount, skins, profileSkins, skinService, originalPlayerProfileService)
				.show(player)
		}
	}
}
