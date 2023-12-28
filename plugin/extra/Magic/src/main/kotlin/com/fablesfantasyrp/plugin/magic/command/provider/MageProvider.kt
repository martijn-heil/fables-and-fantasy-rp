package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider

class MageProvider(private val characterProvider: Provider<Character>,
				   private val characters: CharacterRepository,
				   private val mages: MageRepository) : Provider<Mage> {
	override val isProvided: Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Mage {
		val character = characterProvider.get(arguments, modifiers)!!
		return mages.forCharacter(character)
				?: throw ArgumentParseException("The provided character is not a mage.")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return characters.all().asSequence()
				.filter { mages.forCharacter(it) != null }
				.map { it.name }
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quoteCommandArgument(it) }
				.toList()
	}
}
