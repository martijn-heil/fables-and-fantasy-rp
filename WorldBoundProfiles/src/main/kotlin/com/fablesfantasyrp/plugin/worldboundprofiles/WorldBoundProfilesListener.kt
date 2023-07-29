package com.fablesfantasyrp.plugin.worldboundprofiles

import com.fablesfantasyrp.plugin.location.location
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.ProfilePrompter
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.profile.event.PostPlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.utils.FLATROOM
import com.fablesfantasyrp.plugin.utils.PLOTS
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.worldboundprofiles.data.WorldRestrictionRule
import com.fablesfantasyrp.plugin.worldboundprofiles.data.WorldRestrictionRuleAction
import com.fablesfantasyrp.plugin.worldboundprofiles.data.WorldRestrictionRuleRepository
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

class WorldBoundProfilesListener(private val plugin: Plugin,
								 private val profiles: EntityProfileRepository,
								 private val profileManager: ProfileManager,
								 private val worldRestrictionRuleRepository: WorldRestrictionRuleRepository) : Listener {
	private val server = plugin.server
	private val ignoreList = HashSet<UUID>()

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	fun onPlayerTeleport(e: PlayerTeleportEvent) {
		if (e.to.world == e.from.world) return
		if (ignoreList.contains(e.player.uniqueId)) return
		val selector = Services.get<ProfilePrompter>() // This is evaluated eagerly on purpose

		val profile = profileManager.getCurrentForPlayer(e.player)
		val fromRule = profile?.let { worldRestrictionRuleRepository.forId(Pair(profile, e.from.world.uid)) }
		val toRule = profile?.let { worldRestrictionRuleRepository.forId(Pair(profile, e.to.world.uid)) }

		if (fromRule?.action == WorldRestrictionRuleAction.BOUND && toRule?.action != WorldRestrictionRuleAction.BOUND) {
			// current profile is bound to the world that the player is trying to leave
			// player must choose a new profile
			val ownProfiles = profiles.activeForOwner(e.player)
			val rules: Map<Profile, Collection<WorldRestrictionRule>> = worldRestrictionRuleRepository.forProfiles(ownProfiles)
			val possible = ownProfiles.filter {
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
			// the world the player is teleporting to requires selecting a profile bound or explicitly allowed to that world
			val allowed = worldRestrictionRuleRepository.getExplicitlyAllowedProfiles(e.to.world, e.player).filter { it.isActive }
			if (allowed.contains(profile)) return
			e.isCancelled = true
			chooseNewAndTeleport(e.player, e.to, allowed, selector)
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	fun onProfileSwitch(e: PlayerSwitchProfileEvent) {
		e.transaction.addToCollection(ignoreList, e.player.uniqueId)
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	fun onPostProfileSwitch(e: PostPlayerSwitchProfileEvent) {
		ignoreList.remove(e.player.uniqueId)
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val ownProfiles = profiles.activeForOwner(e.player)
		val rules = worldRestrictionRuleRepository.forProfiles(ownProfiles)

		if (FLATROOM != null && rules.values.find { it.find { it.id.second == FLATROOM!!.uid && it.action == WorldRestrictionRuleAction.BOUND } != null } == null) {
			val newProfile = this.profiles.create(Profile(
					owner = e.player,
					description = "Builder",
					isActive = true
			))
			newProfile.location = PLOTS!!.spawnLocation
			worldRestrictionRuleRepository.updateOrCreate(WorldRestrictionRule(Pair(newProfile, FLATROOM!!.uid), WorldRestrictionRuleAction.BOUND))
			worldRestrictionRuleRepository.updateOrCreate(WorldRestrictionRule(Pair(newProfile, PLOTS!!.uid), WorldRestrictionRuleAction.BOUND))
		}
	}

	private fun chooseNewAndTeleport(player: Player, to: Location, allowed: Collection<Profile>, selector: ProfilePrompter) {
		plugin.launch {
			val newProfile = when (allowed.size) {
				1 -> allowed.first()
				else -> selector.promptSelect(player, allowed)
			}
			profileManager.setCurrentForPlayer(player, newProfile)
			ignoreList.add(player.uniqueId)
			try {
				player.teleport(to)
			} finally {
				ignoreList.remove(player.uniqueId)
			}
		}
	}
}
