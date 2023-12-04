package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BasePermissionProvidingTrait
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.hacks.PermissionInjector
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

class SpidahRidah(plugin: Plugin,
				  characters: CharacterRepository,
				  profileManager: ProfileManager,
				  permissionInjector: PermissionInjector)
	: BasePermissionProvidingTrait(CharacterTrait.SPIDAH_RIDAH, plugin, characters, profileManager, permissionInjector) {

	override val permission: String = "mcpets.voras"
}
