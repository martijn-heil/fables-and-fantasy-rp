package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.command.provider.PlayerCharacterProvider
import com.fablesfantasyrp.plugin.characters.command.provider.quote
import com.fablesfantasyrp.plugin.characters.playerCharacters
import com.fablesfantasyrp.plugin.magic.data.entity.Mage
import com.fablesfantasyrp.plugin.magic.mageRepository
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.Bukkit

class MageProvider(private val characterProvider: PlayerCharacterProvider) : Provider<Mage> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Mage {
		val character = characterProvider.get(arguments, modifiers)!!
		return mageRepository.forPlayerCharacter(character)
				?: throw ArgumentParseException("The provided character is not a mage.")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return Bukkit.getServer().playerCharacters.asSequence()
				.filter { mageRepository.forPlayerCharacter(it) != null }
				.map { it.name }
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quote(it) }
				.toList()
	}
}
