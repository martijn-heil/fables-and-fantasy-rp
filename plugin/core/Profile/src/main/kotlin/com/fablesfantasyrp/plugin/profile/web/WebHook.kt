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

import com.fablesfantasyrp.plugin.web.loaders.WebRequestValidationLoader
import com.fablesfantasyrp.plugin.web.loaders.WebRoutingLoader
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal class WebHook {
	private lateinit var koinModule: Module

	fun start() {
		koinModule = module(createdAtStart = true) {
			singleOf(::WebRouting) bind WebRoutingLoader::class
			singleOf(::WebRequestValidation) bind WebRequestValidationLoader::class
		}
		loadKoinModules(koinModule)
	}
}
