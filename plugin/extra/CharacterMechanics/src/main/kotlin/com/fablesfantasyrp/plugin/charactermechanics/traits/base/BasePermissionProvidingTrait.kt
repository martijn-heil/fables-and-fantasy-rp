package com.fablesfantasyrp.plugin.charactermechanics.traits.base

import com.fablesfantasyrp.plugin.charactermechanics.frunBlocking
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.event.CharacterChangeTraitsEvent
import com.fablesfantasyrp.plugin.hacks.PermissionInjector
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import com.fablesfantasyrp.plugin.utils.TransactionStep
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

abstract class BasePermissionProvidingTrait(trait: CharacterTrait,
											plugin: Plugin,
											characters: CharacterRepository,
											profileManager: ProfileManager,
											private val permissionInjector: PermissionInjector)
	: BaseTraitBehavior(trait, plugin, characters, profileManager) {

	abstract val permission: String

	override fun init() {
		super.init()
		server.pluginManager.registerEvents(BasePermissionProvidingTraitListener(), plugin)
	}

	inner class BasePermissionProvidingTraitListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerProfileChange(e: PlayerSwitchProfileEvent) {
			frunBlocking {
				val oldCharacter = e.old?.let { characters.forProfile(it) }
				val newCharacter = e.new?.let { characters.forProfile(it) }

				val oldValue = if (oldCharacter != null && oldCharacter.traits.contains(trait)) true else null
				val value = if (newCharacter != null && newCharacter.traits.contains(trait)) true else null

				e.transaction.steps.add(TransactionStep(
					{ permissionInjector.inject(e.player, permission, value) },
					{ permissionInjector.inject(e.player, permission, oldValue) }
				))
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onCharacterChangeTraits(e: CharacterChangeTraitsEvent) {
			val player = profileManager.getCurrentForProfile(e.character.profile) ?: return
			val value = if (e.character.traits.contains(trait)) true else null
			permissionInjector.inject(player, permission, value)
		}
	}
}
