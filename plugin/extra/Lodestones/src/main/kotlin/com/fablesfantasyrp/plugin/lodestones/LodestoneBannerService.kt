package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.lodestones.domain.entity.MapBox
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.MapBoxRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.worldguardinterop.toBlockVector3
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.util.*

class LodestoneBannerService(private val plugin: Plugin,
							 private val profileManager: ProfileManager,
							 private val characters: CharacterRepository,
							 private val characterLodestoneRepository: CharacterLodestoneRepository,
							 private val lodestoneBanners: LodestoneBannerRepository,
							 private val mapBoxes: MapBoxRepository) {
	private val server = plugin.server
	private val transparentMaterials = setOf(Material.AIR, Material.BARRIER)
	private val previousWalkSpeed = HashMap<UUID, Float>()

	fun init() {
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			val boxes = mapBoxes.all()
			val players = server.onlinePlayers.filter { isInMapBox(it.location, boxes) }

			for (player in players) {
				val profile = profileManager.getCurrentForPlayer(player)
				val character = profile?.let { characters.forProfile(it) }
				val characterLodestones = character?.let { characterLodestoneRepository.forCharacter(it) }
				val banner = getTargetedBanner(player) ?: continue

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

		server.pluginManager.registerEvents(LodestoneBannerServiceListener(), plugin)
	}

	private fun isInMapBox(location: Location, boxes: Collection<MapBox>)
		= boxes.any { box -> box.region.region.contains(location.toBlockVector3()) }

	private fun getTargetedBanner(player: Player): LodestoneBanner? {
		val block = player.getTargetBlock(transparentMaterials, 50)
		if (!Tag.BANNERS.isTagged(block.type)) return null
		return lodestoneBanners.near(block.location)
	}

	inner class LodestoneBannerServiceListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerRightClick(e: PlayerInteractEvent) {
			if (e.hand != EquipmentSlot.HAND) return
			if (e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return
			if (!isInMapBox(e.player.location, mapBoxes.all())) return

			val banner = getTargetedBanner(e.player) ?: return

			e.player.teleport(banner.lodestone.location.toLocation())
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerTeleport(e: PlayerTeleportEvent) {
			val boxes = mapBoxes.all()
			val toMapBox = isInMapBox(e.to, boxes)
			val fromMapBox = isInMapBox(e.from, boxes)

			if (toMapBox && !fromMapBox) {
				previousWalkSpeed[e.player.uniqueId] = e.player.walkSpeed
				e.player.walkSpeed = 0.5f
			} else if (fromMapBox && !toMapBox) {
				val walkSpeed = previousWalkSpeed.remove(e.player.uniqueId) ?: 0.2f
				e.player.walkSpeed = walkSpeed
			}
		}
	}
}
