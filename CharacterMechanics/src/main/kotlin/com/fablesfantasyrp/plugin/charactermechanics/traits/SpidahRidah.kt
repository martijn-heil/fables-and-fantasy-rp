package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.hacks.PermissionInjector
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

class SpidahRidah(plugin: Plugin,
				  characters: CharacterRepository,
				  profileManager: ProfileManager,
				  traits: CharacterTraitRepository,
				  private val permissionInjector: PermissionInjector)
	: BaseTraitBehaviour(KnownCharacterTraits.SPIDAH_RIDAH, plugin, characters, profileManager, traits) {

	override fun init() {
		super.init()
		permissionInjector.inject("mcpets.voras") { if (hasTrait(it)) true else null }
	}
}
