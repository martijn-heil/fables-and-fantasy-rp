package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.CharacterRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider

class MageProvider(private val characterProvider: Provider<Character>,
				   private val characters: CharacterRepository,
				   private val mages: MageRepository) : Provider<Mage> {
	override fun isProvided(): Boolean = false

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
