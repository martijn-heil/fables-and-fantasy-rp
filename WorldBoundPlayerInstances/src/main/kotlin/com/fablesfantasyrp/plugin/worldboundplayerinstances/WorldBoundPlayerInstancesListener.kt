package com.fablesfantasyrp.plugin.worldboundplayerinstances

import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceSelectionPrompter
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.event.PostPlayerSwitchPlayerInstanceEvent
import com.fablesfantasyrp.plugin.playerinstance.event.PrePlayerSwitchPlayerInstanceEvent
import com.fablesfantasyrp.plugin.utils.FLATROOM
import com.fablesfantasyrp.plugin.utils.PLOTS
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.WorldRestrictionRule
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.WorldRestrictionRuleAction
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.WorldRestrictionRuleRepository
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.Plugin
import java.util.*

class WorldBoundPlayerInstancesListener(private val plugin: Plugin,
										private val playerInstanceRepository: EntityPlayerInstanceRepository,
										private val playerInstanceManager: PlayerInstanceManager,
										private val worldRestrictionRuleRepository: WorldRestrictionRuleRepository) : Listener {
	private val server = plugin.server
	private val ignoreList = HashSet<UUID>()

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	fun onPlayerTeleport(e: PlayerTeleportEvent) {
		if (e.to.world == e.from.world) return
		if (ignoreList.contains(e.player.uniqueId)) return
		val selector = Services.get<PlayerInstanceSelectionPrompter>() // This is evaluated eagerly on purpose

		val playerInstance = playerInstanceManager.getCurrentForPlayer(e.player)
		val fromRule = playerInstance?.let { worldRestrictionRuleRepository.forId(Pair(playerInstance, e.from.world.uid)) }
		val toRule = playerInstance?.let { worldRestrictionRuleRepository.forId(Pair(playerInstance, e.to.world.uid)) }

		if (fromRule?.action == WorldRestrictionRuleAction.BOUND && toRule?.action != WorldRestrictionRuleAction.BOUND) {
			// current player instance is bound to the world that the player is trying to leave
			// player must choose a new player instance
			val ownedPlayerInstances = playerInstanceRepository.forOwner(e.player)
			val rules: Map<PlayerInstance, Collection<WorldRestrictionRule>> = worldRestrictionRuleRepository.forPlayerInstances(ownedPlayerInstances)
			val possible = ownedPlayerInstances.filter {
				var isBoundAnywhere = false
				var isBoundToDestination = false
				var isAllowedToDestination = false
				var isDeniedToDestination = false
				for (rule in rules[it] ?: emptyList()) {
					val world = server.getWorld(rule.id.second) ?: continue
					if (world == e.to.world && rule.action == WorldRestrictionRuleAction.BOUND) isBoundToDestination = true
					if (world == e.to.world && rule.action == WorldRestrictionRuleAction.ALLOWED) isAllowedToDestination = true
					if (world == e.to.world && rule.action == WorldRestrictionRuleAction.DENIED) isDeniedToDestination = true
					if (rule.action == WorldRestrictionRuleAction.BOUND) isBoundAnywhere = true
				}

				if (isBoundAnywhere) return@filter isBoundToDestination || isAllowedToDestination else !isDeniedToDestination
			}
			e.isCancelled = true
			chooseNewAndTeleport(e.player, e.to, possible, selector)
		} else if (e.to.world == FLATROOM || e.to.world == PLOTS) {
			// the world the player is teleporting to requires selecting a player instance bound or explicitly allowed to that world
			val allowed = worldRestrictionRuleRepository.getExplicitlyAllowedPlayerInstances(e.to.world, e.player)
			if (allowed.contains(playerInstance)) return
			e.isCancelled = true
			chooseNewAndTeleport(e.player, e.to, allowed, selector)
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	fun onPrePlayerInstanceSwitch(e: PrePlayerSwitchPlayerInstanceEvent) {
		ignoreList.add(e.player.uniqueId)
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	fun onPostPlayerInstanceSwitch(e: PostPlayerSwitchPlayerInstanceEvent) {
		ignoreList.remove(e.player.uniqueId)
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val playerInstances = playerInstanceRepository.forOwner(e.player)
		val rules = worldRestrictionRuleRepository.forPlayerInstances(playerInstances)

		if (FLATROOM != null && rules.values.find { it.find { it.id.second == FLATROOM!!.uid && it.action == WorldRestrictionRuleAction.BOUND } != null } == null) {
			val newInstance = playerInstanceRepository.create(PlayerInstance(
					owner = e.player,
					description = "Builder",
					isActive = true
			))
			newInstance.location = PLOTS!!.spawnLocation
			worldRestrictionRuleRepository.updateOrCreate(WorldRestrictionRule(Pair(newInstance, FLATROOM!!.uid), WorldRestrictionRuleAction.BOUND))
			worldRestrictionRuleRepository.updateOrCreate(WorldRestrictionRule(Pair(newInstance, PLOTS!!.uid), WorldRestrictionRuleAction.BOUND))
		}
	}

	private fun chooseNewAndTeleport(player: Player, to: Location, allowed: Collection<PlayerInstance>, selector: PlayerInstanceSelectionPrompter) {
		plugin.launch {
			val newPlayerInstance = selector.promptSelect(player, allowed)
			playerInstanceManager.setCurrentForPlayer(player, newPlayerInstance)
			ignoreList.add(player.uniqueId)
			try {
				player.teleport(to)
			} finally {
				ignoreList.remove(player.uniqueId)
			}
		}
	}
}
