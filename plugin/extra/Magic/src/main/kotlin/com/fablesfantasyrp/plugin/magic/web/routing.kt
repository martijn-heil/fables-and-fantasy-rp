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

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.dal.repository.SpellDataRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.web.model.WebMage
import com.fablesfantasyrp.plugin.magic.web.model.transform
import com.fablesfantasyrp.plugin.web.loaders.BaseWebRoutingLoader
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin

@Resource("/mages")
private class Mages() {
	@Resource("{id}")
    class Id(val parent: Mages = Mages(), val id: Int)
}

internal class WebRouting(private val plugin: Plugin,
				 private val mages: MageRepository,
				 private val characters: CharacterRepository,
				 private val spells: SpellDataRepository) : BaseWebRoutingLoader() {
	private val logger = plugin.logger

	override val routes: Route.() -> Unit = {
		get<Mages> {
			val results = mages.all().map { it.transform() }
			call.respond(results)
		}

		get<Mages.Id> {
			val result = mages.forId(it.id.toLong())?.transform() ?: run {
				call.respond(HttpStatusCode.NotFound)
				return@get
			}
			call.respond(result)
		}

		post<Mages.Id> {
			val webMage = call.receive<WebMage>()
			withContext(plugin.minecraftDispatcher) {
				val character = characters.forId(webMage.id.toInt())!!

				mages.create(Mage(
					character = character,
					magicPath = webMage.magicPath,
					magicLevel = webMage.magicLevel,
					spells = spells.forIds(webMage.spells.asSequence()).toList()
				))
			}

			call.respond(HttpStatusCode.Created)
		}

		put<Mages.Id> {
			val webMage = call.receive<WebMage>()
			withContext(plugin.minecraftDispatcher) {
				val existingMage = mages.forId(it.id.toLong()) ?: run {
					call.respond(HttpStatusCode.NotFound)
					return@withContext
				}

				existingMage.magicLevel = webMage.magicLevel
				existingMage.magicPath = webMage.magicPath
				existingMage.spells = webMage.spells.mapNotNull { spells.forId(it) }
			}

			call.respond(HttpStatusCode.OK)
		}
	}
}
