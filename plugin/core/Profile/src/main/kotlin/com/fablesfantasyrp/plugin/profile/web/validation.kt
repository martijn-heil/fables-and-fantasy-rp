/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.profile.web

import com.fablesfantasyrp.plugin.profile.web.model.WebProfile
import com.fablesfantasyrp.plugin.web.loaders.BaseRequestValidationLoader
import io.ktor.server.plugins.requestvalidation.*
import java.util.*

internal class WebRequestValidation : BaseRequestValidationLoader() {
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
