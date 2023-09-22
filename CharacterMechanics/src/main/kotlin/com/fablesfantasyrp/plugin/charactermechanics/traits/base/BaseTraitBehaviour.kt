package com.fablesfantasyrp.plugin.charactermechanics.traits.base

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.charactertraits.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

abstract class BaseTraitBehaviour(
	private val traitId: String,
	protected val plugin: Plugin,
	protected val characters: CharacterRepository,
	protected val profileManager: ProfileManager,
	protected val traits: CharacterTraitRepository) : TraitBehavior {

	protected val server = plugin.server
	protected lateinit var trait: CharacterTrait


	override fun init() {
		trait = traits.forId(traitId) ?: throw IllegalStateException()
	}

	protected fun getPlayersWithTrait(trait: CharacterTrait = this.trait): Sequence<ActiveTraitHolder> {
		return server.onlinePlayers.asSequence().mapNotNull {
			val profile = profileManager.getCurrentForPlayer(it) ?: return@mapNotNull null
			val character = characters.forProfile(profile) ?: return@mapNotNull null
			if (!traits.forCharacter(character).contains(trait)) return@mapNotNull null
			ActiveTraitHolder(it, character)
		}
	}

	protected fun hasTrait(player: Player): Boolean {
		val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return false
		return traits.hasTrait(character, trait)
	}

	protected data class ActiveTraitHolder(val player: Player, val character: Character)
}
