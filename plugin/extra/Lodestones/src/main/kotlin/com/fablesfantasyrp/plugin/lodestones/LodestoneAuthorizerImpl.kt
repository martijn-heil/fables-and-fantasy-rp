package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile

class LodestoneAuthorizerImpl(private val characters: CharacterRepository,
							  private val characterLodestones: CharacterLodestoneRepository) : LodestoneAuthorizer {
	override fun mayWarpTo(who: Profile?, lodestone: Lodestone): Boolean {
		val character = who?.let { characters.forProfile(who) } ?: return true
		return lodestone.isPublic || characterLodestones.forCharacter(character).contains(lodestone)
	}
}
