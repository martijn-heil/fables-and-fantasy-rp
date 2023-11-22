package com.fablesfantasyrp.plugin.profile.web

import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.web.model.WebProfile
import com.fablesfantasyrp.plugin.profile.web.model.transform
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
import java.util.*

@Resource("/profiles")
private class Profiles() {
	@Resource("{id}")
    class Id(val parent: Profiles = Profiles(), val id: Int)
}

@Resource("/players")
private class Players() {
	@Resource("{id}")
	class Id(val parent: Players = Players(), val id: String)
}

internal class WebRouting(private val plugin: Plugin, private val profiles: EntityProfileRepository) : BaseWebRoutingLoader() {
	private val logger = plugin.logger
	private val server = plugin.server

	override val routes: Route.() -> Unit = {
		get<Profiles> {
			val results = withContext(plugin.minecraftDispatcher) { profiles.all().map { it.transform() } }
			call.respond(results)
		}

		get<Profiles.Id> {
			val result = withContext(plugin.minecraftDispatcher) {
				profiles.forId(it.id)?.transform() ?: run { return@withContext null }
			}

			if (result == null) {
				call.respond(HttpStatusCode.NotFound)
				return@get
			}

			call.respond(result)
		}

		post<Profiles.Id> {
			val webProfile = call.receive<WebProfile>()
			val result = withContext(plugin.minecraftDispatcher) {
				val profile = profiles.forId(it.id) ?: return@withContext HttpStatusCode.NotFound
				profile.description = webProfile.description
				profile.owner = webProfile.owner?.let { server.getOfflinePlayer(UUID.fromString(it)) }
				HttpStatusCode.OK
			}
			call.respond(result)
		}

		get<Players> {
			val results = withContext(plugin.minecraftDispatcher) { server.offlinePlayers.map { it.transform() } }
			call.respond(results)
		}

		get<Players.Id> {
			val uuid = try {
				 UUID.fromString(it.id)
			} catch (ex: IllegalArgumentException) {
				call.respond(HttpStatusCode.BadRequest)
				return@get
			}
			val result = withContext(plugin.minecraftDispatcher) { server.getOfflinePlayer(uuid).transform() }
			call.respond(result)
		}
	}
}
