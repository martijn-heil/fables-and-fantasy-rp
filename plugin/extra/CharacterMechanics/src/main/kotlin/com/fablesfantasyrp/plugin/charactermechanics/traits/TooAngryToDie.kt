package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.knockout.knockout
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isRealPlayer
import org.bukkit.EntityEffect
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.plugin.Plugin

class TooAngryToDie(plugin: Plugin,
					characters: CharacterRepository,
					profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.TOO_ANGRY_TO_DIE, plugin, characters, profileManager) {

	override fun init() {
		super.init()

		server.pluginManager.registerEvents(TooAngryToDieListener(), plugin)
	}

	private fun activate(player: Player) {
		player.playEffect(EntityEffect.TOTEM_RESURRECT)
	}

	inner class TooAngryToDieListener : Listener {
		@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
		fun onPlayerDamageByEntity(e: EntityDamageByEntityEvent) {
			val player = e.entity as? Player ?: return
			if (!player.isRealPlayer) return
			if (player.knockout.isKnockedOut) return

			val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return

			if (!character.traits.contains(trait)) return

			if (player.health - e.finalDamage <= 0) {
				e.isCancelled = true
				activate(player)
			}
		}

		@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
		fun onPlayerDamageByBlock(e: EntityDamageByBlockEvent) {
			val player = e.entity as? Player ?: return
			if (!player.isRealPlayer) return
			if (player.knockout.isKnockedOut) return

			val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return

			if (!character.traits.contains(trait)) return

			if (player.health - e.finalDamage <= 0) {
				e.isCancelled = true
				activate(player)
			}
		}

		@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
		fun onPlayerDamage(e: EntityDamageEvent) {
			if (e is EntityDamageByBlockEvent || e is EntityDamageByEntityEvent) return
			val player = e.entity as? Player ?: return
			if (!player.isRealPlayer) return
			if (player.knockout.isKnockedOut) return

			val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return

			if (!character.traits.contains(trait)) return

			if (player.health - e.finalDamage <= 0) {
				e.isCancelled = true
				activate(player)
			}
		}
	}
}
