package com.fablesfantasyrp.plugin.web.plugins

import com.fablesfantasyrp.plugin.web.PLUGIN
import com.fablesfantasyrp.plugin.web.UserSession
import com.fablesfantasyrp.plugin.web.loaders.WebRoutingLoader
import com.fablesfantasyrp.plugin.web.redirects
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.core.context.GlobalContext

fun Application.configureRouting(httpClient: HttpClient) {
	routing {
		authenticate("auth-oauth-discord") {
			get("/login") {
				// Redirects to 'authorizeUrl' automatically
			}

			get("/callback") {
				val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
				val state = principal?.state
				if (state != null) {
					val session = UserSession(state, principal.accessToken)

					val userInfo = getUserInfo(session, httpClient)
					if (!ALLOWED_USERS.contains(userInfo.id)) {
						call.respond(HttpStatusCode.Forbidden)
					}

					call.sessions.set(session)

					val redirectUrlString = redirects[state]
					if (redirectUrlString != null) {
						call.respondRedirect(redirectUrlString)
					}
				}
			}
		}

		authenticate("auth-session") {
			get("/time") {
				val time = withContext(PLUGIN.minecraftDispatcher) {
					PLUGIN.server.worlds.find { it.name == "world" }!!.time
				}
				call.respondText("Current time in world: $time")
			}

			GlobalContext.get().getAll<WebRoutingLoader>().forEach { it.load()(this) }
		}

		get("/{...}") {
			val userSession: UserSession? = call.sessions.get()
			if (userSession != null) {
				val userInfo = Json.parseToJsonElement(httpClient.get("https://discord.com/api/oauth2/@me") {
					accept(ContentType.Application.Json)
					headers {
						append(HttpHeaders.Authorization, "Bearer ${userSession.token}")
					}
				}.bodyAsText())
				val id = userInfo.jsonObject["user"]!!.jsonObject["id"]!!.jsonPrimitive.content
				val username = userInfo.jsonObject["user"]!!.jsonObject["username"]!!.jsonPrimitive.content
				val discriminator = userInfo.jsonObject["user"]!!.jsonObject["discriminator"]!!.jsonPrimitive.content
				val name = "$username#$discriminator"
				call.respondText("Hello, $name! Your id is $id")
			} else {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}
	}
}
