package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.utils.TransactionStep
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SeaLegs(plugin: Plugin,
			  characters: CharacterRepository,
			  profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.SEA_LEGS, plugin, characters, profileManager) {
	private val effect = PotionEffect(PotionEffectType.DOLPHINS_GRACE, 200, 0, false, false, true)

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

			if (newCharacter != null && newCharacter.traits.contains(trait)) {
				val oldValue = player.maximumAir
				e.transaction.steps.add(TransactionStep({
					player.maximumAir = 600 // 30 seconds of breath
				}, {
					player.maximumAir = oldValue
				}))
			} else {
				val oldValue = player.maximumAir
				e.transaction.steps.add(TransactionStep({
					player.maximumAir = 300 // this is the default of 15 seconds
				}, {
					player.maximumAir = oldValue
				}))
			}
		}
	}
}
