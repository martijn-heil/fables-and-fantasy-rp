package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.plugin.Plugin

class LodestoneBannerService(private val plugin: Plugin,
							 private val profileManager: ProfileManager,
							 private val characters: CharacterRepository,
							 private val characterLodestoneRepository: CharacterLodestoneRepository,
							 private val lodestoneBanners: LodestoneBannerRepository) {
	private val server = plugin.server
	private val transparentMaterials = setOf(Material.AIR, Material.BARRIER)

	fun init() {
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			for (player in server.onlinePlayers) {
				val block = player.getTargetBlock(transparentMaterials, 50)
				if (!Tag.BANNERS.isTagged(block.type)) continue
				val banner = lodestoneBanners.forLocation(block.location.toBlockIdentifier()) ?: continue
				server.sendActionBar(Component.text("Left click to warp to ${banner.lodestone.name}").color(NamedTextColor.RED))
			}
		}, 0, 1)
	}
}
