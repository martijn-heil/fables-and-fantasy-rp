package com.fablesfantasyrp.plugin.charactermechanics.racial.sylvani

import com.fablesfantasyrp.plugin.charactermechanics.racial.base.BasePermissionProvidingRaceBehavior
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.hacks.PermissionInjector
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

class SylvaniSkogPet(plugin: Plugin,
					 characters: CharacterRepository,
					 profileManager: ProfileManager,
					 permissionInjector: PermissionInjector)
	: BasePermissionProvidingRaceBehavior(Race.SYLVANI, plugin, characters, profileManager, permissionInjector) {

	override val permission: String = "mcpets.skog"
}
