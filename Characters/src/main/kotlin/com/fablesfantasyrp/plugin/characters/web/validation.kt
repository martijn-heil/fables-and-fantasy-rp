package com.fablesfantasyrp.plugin.characters.web

import com.fablesfantasyrp.plugin.characters.NAME_DISALLOWED_CHARACTERS
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.characters.web.model.WebCharacter
import com.fablesfantasyrp.plugin.web.loaders.BaseRequestValidationLoader
import io.ktor.server.plugins.requestvalidation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WebRequestValidation(private val characters: EntityCharacterRepository) : BaseRequestValidationLoader() {
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

			if (it.age < 13U) {
				return@validate ValidationResult.Invalid("Character age must be at least 13 years old.")
			}

			if (it.age > 1000U) {
				return@validate ValidationResult.Invalid("Character age must not be greater than 1000 years old.")
			}

			val isNameConflict = withContext(Dispatchers.Main) {
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
