package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.domain.entity.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider

class CharacterTraitProvider(private val traits: CharacterTraitRepository) : Provider<CharacterTrait> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: MutableList<out Annotation>?): CharacterTrait {
		val id = arguments.next()
		return traits.forId(id) ?: throw ArgumentParseException("Character trait '$id' does not exist.")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String>
		= traits.allIds().filter { it.startsWith(prefix.removePrefix("\""), true) }
}
