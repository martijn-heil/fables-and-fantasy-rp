package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.isStaffCharacter
import com.fablesfantasyrp.plugin.lodestones.domain.entity.MapBox
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.MapBoxRepository
import com.fablesfantasyrp.plugin.lodestones.gui.LodestoneGui
import com.fablesfantasyrp.plugin.lodestones.item.WarpCrystal
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.timers.CancelReason
import com.fablesfantasyrp.plugin.timers.CountdownBusyException
import com.fablesfantasyrp.plugin.timers.countdown
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin
import org.ocpsoft.prettytime.PrettyTime
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class LodestoneListener(private val plugin: JavaPlugin,
						private val lodestones: LodestoneRepository,
						private val profileManager: ProfileManager,
						private val characters: CharacterRepository,
						private val mapBoxes: MapBoxRepository,
						private val characterLodestoneRepository: CharacterLodestoneRepository,
						private val authorizer: LodestoneAuthorizer,
						private val slotCountCalculator: LodestoneSlotCountCalculator) : Listener {
	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerRightClickLodestone(e: PlayerInteractEvent) {
		if (e.hand != EquipmentSlot.HAND) return
		if (e.item == null) return
		if (!WarpCrystal.matches(e.item!!)) return
		val block = e.clickedBlock ?: return
		if (block.type != Material.LODESTONE) return
		val lodestone = lodestones.forLocation(block.location.toBlockIdentifier()) ?: return
		e.isCancelled = true

		val character = profileManager.getCurrentForPlayer(e.player)?.let { characters.forProfile(it) } ?: run {
			e.player.sendError("You are not in-character so you cannot use your warp crystal.")
			return
		}

		val slots = slotCountCalculator.getLodestoneSlots(e.player)
		LodestoneGui(plugin, e.player, character, slots, lodestone, characterLodestoneRepository).show(e.player)
	}

	private val lastWarpedAt = HashMap<Int, Instant>()

	@EventHandler(priority = NORMAL, ignoreCancelled = false)
	fun onPlayerRightClick(e: PlayerInteractEvent) {
		if (e.hand != EquipmentSlot.HAND) return
		if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return
		if (e.item == null) return
		if (!WarpCrystal.matches(e.item!!)) return
		if (e.clickedBlock?.type == Material.LODESTONE) return
		val mapBox = mapBoxes.forWorld(e.player.location.world) ?: return
		val profileId = profileManager.getCurrentForPlayer(e.player)?.id

		val canWarpAgainAt = profileId?.let { lastWarpedAt[it]?.plus(10, ChronoUnit.MINUTES) }
		if (authorizer.useCoolDown(e.player) && canWarpAgainAt != null && canWarpAgainAt.isAfter(Instant.now())) {
			e.player.sendError("Your warpcrystal is on cooldown! You can warp again ${PrettyTime().format(canWarpAgainAt)}")
			return
		}

		e.isCancelled = true
		plugin.launch { warpToMapBox(e.player, mapBox) }
	}

	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerExecuteCommand(e: PlayerCommandPreprocessEvent) {
		if (e.message == "/warp" && e.player.hasPermission(Permission.Command.Warp)) {
			e.isCancelled = true
			val mapBox = mapBoxes.forWorld(e.player.world) ?: return
			e.player.teleport(mapBox.location)
		}
	}

	private suspend fun warpToMapBox(player: Player, mapBox: MapBox) {
		try {
			val profileId = profileManager.getCurrentForPlayer(player)?.id
			val useCoolDown = authorizer.useCoolDown(player)

			val delay = if (useCoolDown) 10U else 3U
			player.countdown(delay, emptyList(), listOf(CancelReason.MOVEMENT, CancelReason.HURT))

			if (profileId != null && useCoolDown) {
				lastWarpedAt[profileId] = Instant.now()
			}

			val location = mapBox.location.toCenterLocation()
			location.yaw = 180f // Face north instead of south
			player.teleport(location)
		} catch (_: CountdownBusyException) {
			player.sendError("You are already waiting on a countdown.")
		}
	}
}
