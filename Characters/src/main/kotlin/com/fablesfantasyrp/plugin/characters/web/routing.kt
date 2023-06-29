package com.fablesfantasyrp.plugin.characters.web

import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
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

class WebRouting(private val plugin: Plugin, private val characters: EntityCharacterRepository) : BaseWebRoutingLoader() {
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
				existingCharacter.age = webCharacter.age
				existingCharacter.gender = webCharacter.gender
				existingCharacter.isDead = webCharacter.isDead
				existingCharacter.isShelved = webCharacter.isShelved

				call.respond(HttpStatusCode.OK)
			}
		}
	}
}
