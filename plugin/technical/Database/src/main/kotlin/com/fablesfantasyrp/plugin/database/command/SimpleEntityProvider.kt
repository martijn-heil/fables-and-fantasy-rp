package com.fablesfantasyrp.plugin.database.command

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider

abstract class SimpleEntityProvider<T, R>(private val repository: R) : Provider<T>
	where T : Identifiable<Int>,
		  R: KeyedRepository<Int, T> {
	override val isProvided: Boolean = false
	abstract val entityName: String

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): T {
		return if (arguments.peek().startsWith("#")) {
			val id = arguments.next().removePrefix("#").toIntOrNull()
				?: throw ArgumentParseException("Could not parse id")
			repository.forId(id) ?: throw ArgumentParseException("$entityName with id #$id could not be found.")
		} else {
			throw ArgumentParseException("Please specify an id")
		}
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return emptyList()
	}
}
