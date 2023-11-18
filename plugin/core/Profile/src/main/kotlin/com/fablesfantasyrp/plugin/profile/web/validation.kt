package com.fablesfantasyrp.plugin.profile.web

import com.fablesfantasyrp.plugin.profile.web.model.WebProfile
import com.fablesfantasyrp.plugin.web.loaders.BaseRequestValidationLoader
import io.ktor.server.plugins.requestvalidation.*
import java.util.*

class WebRequestValidation : BaseRequestValidationLoader() {
	override val validation: RequestValidationConfig.() -> Unit = {
		validate<WebProfile> {
			if (it.description != null && it.description.length > 255) {
				return@validate ValidationResult.Invalid("Description may not be longer than 255 characters.")
			}

			if (it.description != null && it.description.isBlank()) {
				return@validate ValidationResult.Invalid("Description must not be blank.")
			}

			if (it.owner != null) {
				try {
					UUID.fromString(it.owner)
				} catch (ex: IllegalArgumentException) {
					return@validate ValidationResult.Invalid("Invalid UUID for owner.")
				}
			}

			ValidationResult.Valid
		}
	}
}
