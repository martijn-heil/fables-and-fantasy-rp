package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.isStaffCharacter
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.entity.Player

class LodestoneAuthorizerImpl(private val characters: CharacterRepository,
							  private val profileManager: ProfileManager,
							  private val characterLodestones: CharacterLodestoneRepository) : LodestoneAuthorizer {
	override fun mayWarpTo(who: Profile?, lodestone: Lodestone): Boolean {
		val character = who?.let { characters.forProfile(who) } ?: return true
		return lodestone.isPublic || character.isStaffCharacter || characterLodestones.forCharacter(character).contains(lodestone)
	}

	override fun useCoolDown(who: Player): Boolean {
		val character = profileManager.getCurrentForPlayer(who)?.let { characters.forProfile(it) }
		return character != null && !character.isStaffCharacter
	}
}
