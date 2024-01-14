package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument

class MageProvider(private val characterProvider: Provider<Character>,
				   private val mages: MageRepository) : Provider<Mage> {
	override val isProvided: Boolean = false

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Mage {
		val character = characterProvider.get(arguments, modifiers)!!
		return mages.forCharacter(character)
				?: throw ArgumentParseException("The provided character is not a mage.")
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return mages.all().asSequence()
			.map { it.character.name }
			.filter { it.startsWith(prefix.removePrefix("\""), true) }
			.map { quoteCommandArgument(it) }
			.toList()
	}
}
