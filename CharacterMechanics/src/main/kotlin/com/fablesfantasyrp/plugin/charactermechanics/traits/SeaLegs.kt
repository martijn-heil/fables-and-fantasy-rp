package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.charactertraits.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SeaLegs(plugin: Plugin,
			  characters: EntityCharacterRepository,
			  profileManager: ProfileManager,
			  traits: CharacterTraitRepository)
	: BaseTraitBehaviour(KnownCharacterTraits.SEA_LEGS, plugin, characters, profileManager, traits) {
	private val effect = PotionEffect(PotionEffectType.DOLPHINS_GRACE, 200, 1, false, false, true)

	override fun init() {
		super.init()

		server.pluginManager.registerEvents(SeaLegsListener(), plugin)

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			getPlayersWithTrait().forEach { it.player.addPotionEffect(effect) }
		}, 0, 1)
	}

	inner class SeaLegsListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerProfileChange(e: PlayerSwitchProfileEvent) {
			val player = e.player
			val newCharacter = e.new?.let { characters.forProfile(it) }

			if (newCharacter != null && traits.hasTrait(newCharacter, trait)) {
				e.transaction.setProperty(player::maximumAir, 600) // 30 seconds of breath
			} else {
				e.transaction.setProperty(player::maximumAir, 300) // this is the default of 15 seconds
			}
		}
	}
}
