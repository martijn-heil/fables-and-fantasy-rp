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
package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.domain.service.GameModeAuthorizer
import com.fablesfantasyrp.plugin.lodestones.domain.entity.MapBox
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneBannerRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.MapBoxRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.every
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.ColumnIdentifier
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.groundLevel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.title.Title
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
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
import kotlin.math.roundToInt
import kotlin.time.toKotlinDuration

private data class PlayerState(val gameMode: GameMode, val walkSpeed: Float, val allowFlight: Boolean)

private val SPECIAL_PLAYER_STATE = PlayerState(
	gameMode = GameMode.ADVENTURE,
	walkSpeed = 0.5f,
	allowFlight = false
)

class LodestoneBannerService(private val plugin: Plugin,
							 private val profileManager: ProfileManager,
							 private val characters: CharacterRepository,
							 private val lodestoneBanners: LodestoneBannerRepository,
							 private val mapBoxes: MapBoxRepository,
							 private val authorizer: LodestoneAuthorizer) {
	private val server = plugin.server
	private val logger = plugin.logger
	private val transparentMaterials = hashSetOf(
		Material.AIR,
		Material.CAVE_AIR,
		Material.VOID_AIR,
		Material.BARRIER,
		Material.LIGHT)
	private val previousState = HashMap<UUID, PlayerState>()

	fun init() {
		every(plugin, Duration.ofMillis(50).toKotlinDuration()) {
			val players = server.onlinePlayers.asSequence()
				.filter { it.gameMode != GameMode.CREATIVE }
				.filter { mapBoxes.anyContains(it.location) }
				.toList()

			for (player in players) {
				val mapBox = mapBoxes.forLocation(player.location)!!
				val profile = profileManager.getCurrentForPlayer(player)

				val targetedLocation = getTargetedLocation(player) ?: continue
				val banner = lodestoneBanners.near(targetedLocation)

				if (banner != null) {
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

					val titleColor = if (canWarp) NamedTextColor.YELLOW else NamedTextColor.GRAY
					player.showTitle(Title.title(
						Component.text(banner.lodestone.name).color(titleColor),
						Component.empty(),
						Title.Times.times(Duration.ZERO, Duration.ofMillis(100), Duration.ZERO)))
					player.sendActionBar(message)
				} else if (profile == null || characters.forProfile(profile) == null) {
					val destination = translateTargetLocation(mapBox, targetedLocation.toCenterLocation())

					player.sendActionBar(miniMessage.deserialize("<gray>Left click to warp to <location></gray>",
						Placeholder.unparsed("location", "${destination.x}, ${destination.z}")))
				}
			}
		}

		server.pluginManager.registerEvents(LodestoneBannerServiceListener(), plugin)
	}

	fun stop() {
		previousState
			.mapNotNull { server.getPlayer(it.key)?.let { p -> Pair(p, it.value) } }
			.forEach { applyPlayerState(it.first, it.second) }
		previousState.clear()
	}

	private fun getTargetedLocation(player: Player): Location? {
		val block = try {
			player.getTargetBlock(transparentMaterials, 100)
		} catch (ex: IllegalStateException) {
			// ignore java.lang.IllegalStateException: Start block missed in BlockIterator
			return null
		}
		return if (block.type == Material.AIR) null else block.location.toCenterLocation()
	}

	private fun applyPlayerState(player: Player, state: PlayerState) {
		val gameModeAuthorizer = Services.getMaybe<GameModeAuthorizer>()
		val world = player.world

		if (gameModeAuthorizer == null) {
			logger.warning("There is no GameModeAuthorizer registered, defaulting to standard behaviour. This can be dangerous.")
		}

		val gameMode = if (state !== SPECIAL_PLAYER_STATE && gameModeAuthorizer != null) {
			if (gameModeAuthorizer.mayAccess(player, state.gameMode, world)) {
				state.gameMode
			} else {
				gameModeAuthorizer.getPreferredGameMode(player, world)
			}
		} else state.gameMode

		player.gameMode = gameMode
		player.walkSpeed = state.walkSpeed
		player.allowFlight = state.allowFlight
	}

	private fun translateTargetLocation(mapBox: MapBox, targetedLocation: Location): ColumnIdentifier {
		val plane = mapBox.plane

		val deltaX = targetedLocation.x - plane.bounds.minX
		val deltaZ = targetedLocation.z - plane.bounds.minZ

		val xRatio = plane.anchor.bounds.width.toDouble() / plane.bounds.width.toDouble()
		val zRatio = plane.anchor.bounds.height.toDouble() / plane.bounds.height.toDouble()

		val world = plane.anchor.world
		val x = plane.anchor.bounds.minX + deltaX * xRatio
		val z = plane.anchor.bounds.minZ + deltaZ * zRatio

		return ColumnIdentifier(world.uid, x.roundToInt(), z.roundToInt())
	}

	inner class LodestoneBannerServiceListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
		fun onPlayerLeftClick(e: PlayerInteractEvent) {
			if (e.hand != EquipmentSlot.HAND) return
			if (e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return
			if (e.player.gameMode == GameMode.CREATIVE) return

			val mapBox = mapBoxes.forLocation(e.player.location) ?: return

			val profile = profileManager.getCurrentForPlayer(e.player)
			val targetedLocation = getTargetedLocation(e.player) ?: return
			val banner = lodestoneBanners.near(targetedLocation)

			if (banner != null) {
				e.isCancelled = true
				flaunch {
					if (authorizer.mayWarpTo(profile, banner.lodestone)) {
						banner.lodestone.warpHere(e.player)
					} else {
						e.player.sendError(
							"You do not have access to warp to ${banner.lodestone.name}. " +
								"Please link this lodestone to your warp crystal first."
						)
					}
				}
			} else if (profile == null || frunBlocking { characters.forProfile(profile) } == null) {
				e.isCancelled = true
				val destination = translateTargetLocation(mapBox, targetedLocation).groundLevel()
				destination.yaw = 180f // face north
				e.player.teleport(destination)
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerTeleport(e: PlayerTeleportEvent) {
			server.scheduler.scheduleSyncDelayedTask(plugin) {
				val toMapBox = mapBoxes.anyContains(e.to)
				val fromMapBox = mapBoxes.anyContains(e.from)

				if (toMapBox && !fromMapBox) {
					previousState[e.player.uniqueId] = PlayerState(
						gameMode = e.player.gameMode,
						walkSpeed = e.player.walkSpeed,
						allowFlight = e.player.allowFlight
					)
					applyPlayerState(e.player, SPECIAL_PLAYER_STATE)
				} else if (fromMapBox && !toMapBox) {
					val previousState = previousState.remove(e.player.uniqueId) ?: return@scheduleSyncDelayedTask
					applyPlayerState(e.player, previousState)
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			if (mapBoxes.anyContains(e.player.location)) {
				val previousState = previousState.remove(e.player.uniqueId) ?: return
				applyPlayerState(e.player, previousState)
			}
		}
	}
}
