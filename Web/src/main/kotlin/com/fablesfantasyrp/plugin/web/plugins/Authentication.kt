package com.fablesfantasyrp.plugin.web.plugins

import com.fablesfantasyrp.plugin.web.UserInfo
import com.fablesfantasyrp.plugin.web.UserSession
import com.fablesfantasyrp.plugin.web.redirects
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal val ALLOWED_USERS = setOf("325953613924925452", "181845465829081088")

fun Application.configureAuth(httpClient: HttpClient,
							  callbackUrl: String,
							  discordClientId: String,
							  discordClientSecret: String) {
	install(Sessions) {
		cookie<UserSession>("user_session", SessionStorageMemory())
	}

	install(Authentication) {
		session<UserSession>("auth-session") {
			validate { session ->
				session
			}
			challenge {
				call.respond(HttpStatusCode.Unauthorized)
			}
		}

		oauth("auth-oauth-discord") {
			urlProvider = { callbackUrl }
			providerLookup = {
				OAuthServerSettings.OAuth2ServerSettings(
						name = "discord",
						authorizeUrl = "https://discord.com/oauth2/authorize",
						accessTokenUrl = "https://discord.com/api/oauth2/token",
						requestMethod = HttpMethod.Post,
						clientId = discordClientId,
						clientSecret = discordClientSecret,
						defaultScopes = listOf("identify"),
						extraTokenParameters = listOf("access_type" to "offline"),
						onStateCreated = { call, state ->
							val redirectUrl = call.request.queryParameters["redirectUrl"]
							if (redirectUrl != null) redirects[state] = redirectUrl
						}
				)
			}
			client = httpClient
		}
	}
}

suspend fun getUserInfo(session: UserSession, httpClient: HttpClient): UserInfo {
	val userInfo = Json.parseToJsonElement(httpClient.get("https://discord.com/api/oauth2/@me") {
		accept(ContentType.Application.Json)
		headers {
			append(HttpHeaders.Authorization, "Bearer ${session.token}")
		}
	}.bodyAsText())
	val id = userInfo.jsonObject["user"]!!.jsonObject["id"]!!.jsonPrimitive.content
	val username = userInfo.jsonObject["user"]!!.jsonObject["username"]!!.jsonPrimitive.content
	val discriminator = userInfo.jsonObject["user"]!!.jsonObject["discriminator"]!!.jsonPrimitive.content
	return UserInfo(id, "${username}#${discriminator}")
}
