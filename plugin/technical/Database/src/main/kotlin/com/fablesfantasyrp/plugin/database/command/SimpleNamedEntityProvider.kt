package com.fablesfantasyrp.plugin.database.command

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.Named
import com.fablesfantasyrp.plugin.database.repository.NamedRepository
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider

abstract class SimpleNamedEntityProvider<T, R>(private val repository: R) : Provider<T>
	where T : Identifiable<Int>,
		  T: Named,
		  R: KeyedRepository<Int, T>,
		  R: NamedRepository<T> {
	override val isProvided: Boolean = false
	abstract val entityName: String

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): T {
		return if (arguments.peek().startsWith("#")) {
			val id = arguments.next().removePrefix("#").toIntOrNull()
				?: throw ArgumentParseException("Could not parse id")
			repository.forId(id) ?: throw ArgumentParseException("$entityName with id #$id could not be found.")
		} else {
			val name = arguments.next()
			repository.forName(name) ?: throw ArgumentParseException("$entityName with name '$name' could not be found.")
		}
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return repository.allNames().asSequence()
			.filter { it.startsWith(prefix.removePrefix("\""), true) }
			.map { quoteCommandArgument(it) }
			.toList()
	}
}
