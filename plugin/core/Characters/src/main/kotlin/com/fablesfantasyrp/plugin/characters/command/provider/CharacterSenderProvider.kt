package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.frunBlocking
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.sk89q.intake.argument.ArgumentException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.ProvisionException
import org.bukkit.entity.Player

class CharacterSenderProvider(private val profileManager: ProfileManager,
							  private val characters: CharacterRepository,
							  private val senderProvider: Provider<Player>) : Provider<Character> {
	override fun isProvided(): Boolean = true

	@Throws(ArgumentException::class, ProvisionException::class)
	override fun get(commandArgs: CommandArgs, modifiers: List<Annotation>): Character {
		val sender: Player = senderProvider.get(commandArgs, modifiers)!!
		return profileManager.getCurrentForPlayer(sender)?.let { frunBlocking { characters.forProfile(it) } }
				?: throw ProvisionException("You must be in-character to execute this command.")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
