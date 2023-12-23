package com.fablesfantasyrp.plugin.party

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.glowing.GlowingManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.HIGH
import org.bukkit.event.EventPriority.LOW
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.Plugin

class PartyListener(private val plugin: Plugin,
					private val parties: PartyRepository,
					private val profileManager: ProfileManager,
					private val characters: CharacterRepository,
					private val glowingManager: GlowingManager) : Listener {
	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerDamageByPlayer(e: EntityDamageByEntityEvent) {
		frunBlocking {
			val damagerEntity = e.damager

			val damager = if (damagerEntity is Projectile && damagerEntity.shooter is Player) {
				damagerEntity.shooter as Player
			} else damagerEntity as? Player ?: return@frunBlocking

			val damaged = e.entity as? Player ?: return@frunBlocking

			val damagerCharacter = profileManager.getCurrentForPlayer(damager)?.let { characters.forProfile(it) } ?: return@frunBlocking
			val damagedCharacter = profileManager.getCurrentForPlayer(damaged)?.let { characters.forProfile(it) } ?: return@frunBlocking
			val damagerParty = parties.forMember(damagerCharacter) ?: return@frunBlocking
			val damagedParty = parties.forMember(damagedCharacter) ?: return@frunBlocking

			if (damagerParty == damagedParty) e.isCancelled = true
		}
	}

	@EventHandler(priority = HIGH, ignoreCancelled = true)
	fun onPlayerRespawn(e: PlayerRespawnEvent) {
		frunBlocking {
			val character = profileManager.getCurrentForPlayer(e.player)?.let { characters.forProfile(it) } ?: return@frunBlocking
			val party = parties.forMember(character) ?: return@frunBlocking

			if (party.useRespawns) {
				if (party.respawns <= 0) {
					if (party.owner != character) {
						party.members = party.members.minus(character)
						e.player.sendMessage("$SYSPREFIX Your party has run out of respawns! You've been kicked.")
					} else {
						e.player.sendMessage("$SYSPREFIX Your party has run out of respawns!")
					}
					return@frunBlocking
				} else {
					party.respawns--
				}
			}

			val respawnLocation = party.respawnLocation ?: return@frunBlocking
			e.respawnLocation = respawnLocation
		}
	}

	@EventHandler(priority = HIGH, ignoreCancelled = true)
	fun onPlayerSwitchProfile(e: PlayerSwitchProfileEvent) {
		flaunch {
			glowingManager.setGlowColor(e.player, null)
			val party = e.new?.let { characters.forProfile(it) }?.let { parties.forMember(it) } ?: return@flaunch
			glowingManager.setGlowColor(e.player, party.color?.chatColor)
		}
	}
}
