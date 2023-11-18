package com.fablesfantasyrp.plugin.magic.command.provider

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.magic.dal.model.SpellData
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider

class SpellDataProvider(private val spellRepository: KeyedRepository<String, SpellData>) : Provider<SpellData> {

	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): SpellData {
		val identifier = arguments.next()
		return spellRepository.forId(identifier) ?:
			throw ArgumentParseException("A spell called '$identifier' could not be found")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return spellRepository.all().filter { it.id.startsWith(prefix) }.map { it.id }
	}
}
