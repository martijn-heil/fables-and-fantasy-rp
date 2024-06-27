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
package com.fablesfantasyrp.plugin.magic.web

import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.web.model.WebMage
import com.fablesfantasyrp.plugin.web.loaders.BaseRequestValidationLoader
import io.ktor.server.plugins.requestvalidation.*
import org.bukkit.plugin.Plugin

internal class WebRequestValidation(private val plugin: Plugin,
						   private val mages: MageRepository,
						   private val spells: SpellDataRepository) : BaseRequestValidationLoader() {
	override val validation: RequestValidationConfig.() -> Unit = {
		validate<WebMage> {
			if (it.spells.any { spellName -> !spells.exists(spellName) }) {
				return@validate ValidationResult.Invalid("Invalid spell")
			}

			ValidationResult.Valid
		}
	}
}
