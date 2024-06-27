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
package com.fablesfantasyrp.plugin.characters.web

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.web.model.WebCharacter
import com.fablesfantasyrp.plugin.characters.web.model.transform
import com.fablesfantasyrp.plugin.web.loaders.BaseWebRoutingLoader
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin

@Resource("/characters")
private class Characters() {
	@Resource("{id}")
    class Id(val parent: Characters= Characters(), val id: Int)
}

internal class WebRouting(private val plugin: Plugin, private val characters: CharacterRepository) : BaseWebRoutingLoader() {
	private val logger = plugin.logger

	override val routes: Route.() -> Unit = {
		get<Characters> {
			val results = characters.all().map { it.transform() }
			call.respond(results)
		}

		get<Characters.Id> {
			val result = characters.forId(it.id)?.transform() ?: run {
				call.respond(HttpStatusCode.NotFound)
				return@get
			}
			call.respond(result)
		}

		post<Characters.Id> {
			val webCharacter = call.receive<WebCharacter>()
			withContext(plugin.minecraftDispatcher) {
				val existingCharacter = characters.forId(it.id) ?: run {
					call.respond(HttpStatusCode.NotFound)
					return@withContext
				}

				existingCharacter.name = webCharacter.name
				existingCharacter.race = webCharacter.race
				existingCharacter.description = webCharacter.description
				existingCharacter.gender = webCharacter.gender
				existingCharacter.isDead = webCharacter.isDead
				existingCharacter.isShelved = webCharacter.isShelved

				call.respond(HttpStatusCode.OK)
			}
		}
	}
}
