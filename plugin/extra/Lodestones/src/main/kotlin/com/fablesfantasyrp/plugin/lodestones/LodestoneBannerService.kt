package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.lodestones.domain.entity.MapBox
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.MapBoxRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
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
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.util.*

class LodestoneBannerService(private val plugin: Plugin,
							 private val profileManager: ProfileManager,
							 private val lodestoneBanners: LodestoneBannerRepository,
							 private val mapBoxes: MapBoxRepository,
							 private val authorizer: LodestoneAuthorizer) {
	private val server = plugin.server
	private val transparentMaterials = setOf(Material.AIR, Material.BARRIER)
	private val previousWalkSpeed = HashMap<UUID, Float>()

	fun init() {
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			val players = server.onlinePlayers.filter { mapBoxes.anyContains(it.location) }

			for (player in players) {
				val profile = profileManager.getCurrentForPlayer(player)
				val banner = getTargetedBanner(player) ?: continue

				val canWarp = authorizer.mayWarpTo(profile, banner.lodestone)

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

	fun stop() {
		previousWalkSpeed
			.mapNotNull { server.getPlayer(it.key) }
			.forEach { restoreWalkSpeed(it) }
		previousWalkSpeed.clear()
	}

	private fun getTargetedBanner(player: Player): LodestoneBanner? {
		val block = player.getTargetBlock(transparentMaterials, 50)
		return lodestoneBanners.near(block.location)
	}

	private fun boostWalkSpeed(player: Player) {
		previousWalkSpeed[player.uniqueId] = player.walkSpeed
		player.walkSpeed = 0.5f
	}

	private fun restoreWalkSpeed(player: Player) {
		val walkSpeed = previousWalkSpeed.remove(player.uniqueId) ?: 0.2f
		player.walkSpeed = walkSpeed
	}

	inner class LodestoneBannerServiceListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
		fun onPlayerLeftClick(e: PlayerInteractEvent) {
			if (e.hand != EquipmentSlot.HAND) return
			if (e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return
			if (!mapBoxes.anyContains(e.player.location)) return

			val banner = getTargetedBanner(e.player) ?: return

			val profile = profileManager.getCurrentForPlayer(e.player)

			e.isCancelled = true
			if (authorizer.mayWarpTo(profile, banner.lodestone)) {
				banner.lodestone.warpHere(e.player)
			} else {
				e.player.sendError(
					"You do not have access to warp to ${banner.lodestone.name}. " +
					"Please link this lodestone to your warp crystal first."
				)
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerTeleport(e: PlayerTeleportEvent) {
			val toMapBox = mapBoxes.anyContains(e.to)
			val fromMapBox = mapBoxes.anyContains(e.from)

			if (toMapBox && !fromMapBox) {
				boostWalkSpeed(e.player)
			} else if (fromMapBox && !toMapBox) {
				restoreWalkSpeed(e.player)
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			if (mapBoxes.anyContains(e.player.location)) {
				restoreWalkSpeed(e.player)
			}
		}
	}
}
