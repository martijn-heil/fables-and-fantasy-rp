package com.fablesfantasyrp.plugin.worldboundplayerinstances

import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceManager
import com.fablesfantasyrp.plugin.utils.FLATROOM
import com.fablesfantasyrp.plugin.utils.PLOTS
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.WorldRestrictionRuleAction
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.WorldRestrictionRuleRepository
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class WorldBoundPlayerInstancesListener(private val playerInstanceManager: PlayerInstanceManager,
										private val worldRestrictionRuleRepository: WorldRestrictionRuleRepository) : Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	fun onPlayerTeleport(e: PlayerTeleportEvent) {
		if (e.to.world == e.from.world) return

		val playerInstance = playerInstanceManager.getCurrentForPlayer(e.player)
		val fromRule = playerInstance?.let { worldRestrictionRuleRepository.forId(Pair(playerInstance, e.from.world.uid)) }
		val toRule = playerInstance?.let { worldRestrictionRuleRepository.forId(Pair(playerInstance, e.to.world.uid)) }

		if (fromRule?.action == WorldRestrictionRuleAction.BOUND && toRule?.action != WorldRestrictionRuleAction.BOUND) {
			// current player instance is bound to the world that the player is trying to leave
			// player must choose a new player instance
		} else if (e.to.world == FLATROOM || e.to.world == PLOTS) {
			// the world the player is teleporting to requires selecting a player instance bound or explicitly allowed to that world
			worldRestrictionRuleRepository.getExplicitlyAllowedPlayerInstances(e.to.world, e.player)
		}
	}
}
