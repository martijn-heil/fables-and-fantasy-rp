package com.fablesfantasyrp.plugin.characters.web

import com.fablesfantasyrp.plugin.characters.NAME_DISALLOWED_CHARACTERS
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.web.model.WebCharacter
import com.fablesfantasyrp.plugin.web.loaders.BaseRequestValidationLoader
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import io.ktor.server.plugins.requestvalidation.*
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin

class WebRequestValidation(private val plugin: Plugin, private val characters: CharacterRepository) : BaseRequestValidationLoader() {
	override val validation: RequestValidationConfig.() -> Unit = {
		validate<WebCharacter> {
			if (it.description.length > 255) {
				return@validate ValidationResult.Invalid("Description may not be longer than 255 characters.")
			}

			if (it.name.isBlank()) {
				return@validate ValidationResult.Invalid("Character name must not be blank.")
			}

			if (it.name.length > 32) {
				return@validate ValidationResult.Invalid("Character name must not be longer than 32 characters.")
			}

			if (NAME_DISALLOWED_CHARACTERS.containsMatchIn(it.name)) {
				return@validate ValidationResult.Invalid("Character name must not contain illegal characters.")
			}

			val isNameConflict = withContext(plugin.minecraftDispatcher) {
				val otherCharacter = characters.forName(it.name)
				otherCharacter != null && otherCharacter.id != it.id
			}
			if (isNameConflict) {
				return@validate ValidationResult.Invalid("Character name must be unique.")
			}

			ValidationResult.Valid
		}
	}
}