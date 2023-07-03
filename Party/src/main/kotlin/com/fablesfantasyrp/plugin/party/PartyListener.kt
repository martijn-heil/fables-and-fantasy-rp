package com.fablesfantasyrp.plugin.party

import com.fablesfantasyrp.plugin.characters.data.entity.CharacterRepository
import com.fablesfantasyrp.plugin.glowing.GlowingManager
import com.fablesfantasyrp.plugin.party.data.PartyRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.HIGH
import org.bukkit.event.EventPriority.LOW
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerRespawnEvent

class PartyListener(private val parties: PartyRepository,
					private val profileManager: ProfileManager,
					private val characters: CharacterRepository,
					private val glowingManager: GlowingManager) : Listener {
	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerDamageByPlayer(e: EntityDamageByEntityEvent) {
		val damagerEntity = e.damager

		val damager = if (damagerEntity is Projectile && damagerEntity.shooter is Player) {
			damagerEntity.shooter as Player
		} else damagerEntity as? Player ?: return

		val damaged = e.entity as? Player ?: return

		val damagerCharacter = profileManager.getCurrentForPlayer(damager)?.let { characters.forProfile(it) } ?: return
		val damagedCharacter = profileManager.getCurrentForPlayer(damaged)?.let { characters.forProfile(it) } ?: return
		val damagerParty = parties.forMember(damagerCharacter) ?: return
		val damagedParty = parties.forMember(damagedCharacter) ?: return

		if (damagerParty == damagedParty) e.isCancelled = true
	}

	@EventHandler(priority = HIGH, ignoreCancelled = true)
	fun onPlayerRespawn(e: PlayerRespawnEvent) {
		val character = profileManager.getCurrentForPlayer(e.player)?.let { characters.forProfile(it) } ?: return
		val party = parties.forMember(character) ?: return

		if (party.useRespawns) {
			if (party.respawns <= 0) {
				if (party.owner != character) {
					party.members = party.members.minus(character)
					e.player.sendMessage("$SYSPREFIX Your party has run out of respawns! You've been kicked.")
				} else {
					e.player.sendMessage("$SYSPREFIX Your party has run out of respawns!")
				}
				return
			} else {
				party.respawns--
			}
		}

		val respawnLocation = party.respawnLocation ?: return
		e.respawnLocation = respawnLocation
	}

	@EventHandler(priority = HIGH, ignoreCancelled = true)
	fun onPlayerSwitchProfile(e: PlayerSwitchProfileEvent) {
		glowingManager.setGlowColor(e.player, null)
		val party = e.new?.let { characters.forProfile(it) }?.let { parties.forMember(it) } ?: return
		glowingManager.setGlowColor(e.player, party.color?.chatColor)
	}
}
