package com.fablesfantasyrp.plugin.bell

import com.fablesfantasyrp.plugin.bell.domain.repository.BellRepository
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.gui.confirm
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin

class BellListener(private val plugin: Plugin,
				   private val bells: BellRepository,
				   private val profileManager: ProfileManager,
				   private val characters: CharacterRepository) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerRightClickBell(e: PlayerInteractEvent) {
		if (e.hand != EquipmentSlot.HAND) return
		val block = e.clickedBlock ?: return
		if (block.type != Material.BELL) return
		val bell = bells.forLocation(block.location.toBlockIdentifier()) ?: return
		e.isCancelled = true

		flaunch {
			val character = profileManager.getCurrentForPlayer(e.player)?.let { characters.forProfile(it) } ?: run {
				e.player.sendError("You are not in-character so you cannot ring the bell.")
				return@flaunch
			}

			if (e.player.confirm("Ring the alarm bell?")) {
				tryRingBell(bell, character)
			}
		}
	}
}
