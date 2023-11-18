package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.item.WarpCrystal
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin

class LodestoneListener(private val plugin: Plugin,
						private val lodestones: LodestoneRepository,
						private val profileManager: ProfileManager,
						private val characters: CharacterRepository) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
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
	}
}
