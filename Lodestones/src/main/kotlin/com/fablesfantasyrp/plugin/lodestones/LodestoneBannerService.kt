package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.miniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.plugin.Plugin
import java.time.Duration

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
				val profile = profileManager.getCurrentForPlayer(player)
				val character = profile?.let { characters.forProfile(it) }
				val characterLodestones = character?.let { characterLodestoneRepository.forCharacter(it) }
				val block = player.getTargetBlock(transparentMaterials, 50)
				if (!Tag.BANNERS.isTagged(block.type)) continue
				val banner = lodestoneBanners.near(block.location) ?: continue

				val canWarp = character == null || characterLodestones!!.contains(banner.lodestone)

				val message = if (canWarp) {
					miniMessage.deserialize("<red>Left click to warp to <name></red>",
						Placeholder.unparsed("name", banner.lodestone.name)
					)
				} else {
					miniMessage.deserialize("<red>[Not unlocked]</red> <gray>Cannot warp to <name></gray>",
						Placeholder.unparsed("name", banner.lodestone.name)
					)
				}

				server.showTitle(Title.title(
					Component.text(banner.lodestone.name).color(NamedTextColor.GRAY),
					Component.empty(),
					Title.Times.times(Duration.ZERO, Duration.ofMillis(100), Duration.ZERO)))
				server.sendActionBar(message)
			}
		}, 0, 1)
	}
}
