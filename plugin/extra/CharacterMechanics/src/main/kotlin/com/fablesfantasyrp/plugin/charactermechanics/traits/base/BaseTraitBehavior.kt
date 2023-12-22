package com.fablesfantasyrp.plugin.charactermechanics.traits.base

import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

abstract class BaseTraitBehavior(
	protected val trait: CharacterTrait,
	protected val plugin: Plugin,
	protected val characters: CharacterRepository,
	protected val profileManager: ProfileManager) : TraitBehavior {

	protected val server = plugin.server


	override fun init() {
	}

	protected suspend fun getPlayersWithTrait(trait: CharacterTrait = this.trait): Flow<ActiveTraitHolder> {
		return server.onlinePlayers.asFlow().mapNotNull {
			val profile = profileManager.getCurrentForPlayer(it) ?: return@mapNotNull null
			val character = characters.forProfile(profile) ?: return@mapNotNull null
			if (!character.traits.contains(trait)) return@mapNotNull null
			ActiveTraitHolder(it, character)
		}
	}

	protected suspend fun hasTrait(player: Player): Boolean {
		val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return false
		return character.traits.contains(trait)
	}

	protected data class ActiveTraitHolder(val player: Player, val character: Character)
}
