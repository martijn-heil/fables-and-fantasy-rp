package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.caturix.argument.ArgumentException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.parametric.ProvisionException
import org.bukkit.entity.Player

class CharacterSenderProvider(private val profileManager: ProfileManager,
							  private val characters: CharacterRepository,
							  private val senderProvider: Provider<Player>) : Provider<Character> {
	override val isProvided: Boolean = true

	@Throws(ArgumentException::class, ProvisionException::class)
	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Character {
		val sender: Player = senderProvider.get(arguments, modifiers)
		return profileManager.getCurrentForPlayer(sender)?.let { characters.forProfile(it) }
				?: throw ProvisionException("You must be in-character to execute this command.")
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
