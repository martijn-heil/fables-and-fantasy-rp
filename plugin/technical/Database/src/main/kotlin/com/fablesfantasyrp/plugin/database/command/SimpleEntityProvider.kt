package com.fablesfantasyrp.plugin.database.command

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider

abstract class SimpleEntityProvider<T, R>(private val repository: R) : Provider<T>
	where T : Identifiable<Int>,
		  R: KeyedRepository<Int, T> {
	override fun isProvided(): Boolean = false
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
