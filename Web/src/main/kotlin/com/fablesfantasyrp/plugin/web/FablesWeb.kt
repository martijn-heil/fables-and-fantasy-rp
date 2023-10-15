package com.fablesfantasyrp.plugin.web

import com.fablesfantasyrp.plugin.web.plugins.configureAuth
import com.fablesfantasyrp.plugin.web.plugins.configureRequestValidation
import com.fablesfantasyrp.plugin.web.plugins.configureRouting
import com.fablesfantasyrp.plugin.web.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

internal val PLUGIN: FablesWeb
	get() = FablesWeb.instance

val redirects = mutableMapOf<String, String>()

class FablesWeb : JavaPlugin(), KoinComponent {
	lateinit var koinModule: Module

	override fun onEnable() {
		instance = this
		saveDefaultConfig()

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesWeb } binds(arrayOf(JavaPlugin::class))

			single {
				HttpClient(CIO) {
					install(ContentNegotiation) {
						json()
					}
				}
			}
		}
		loadKoinModules(koinModule)

		val discordClientId = config.getString("discord.client_id")!!
		val discordClientSecret = config.getString("discord.client_secret")!!
		val oAuth2Callback = config.getString("discord.oauth2_callback")!!
		val port = config.getInt("bind.port")
		val host = config.getString("bind.host")!!
		val allowHosts = config.getStringList("allowHosts")

		server.scheduler.scheduleSyncDelayedTask(this, {
			embeddedServer(Netty, port = port, host = host) {
				install(Resources)

				install(CORS) {
					allowHosts.forEach { allowHost(it) }
					methods.addAll(HttpMethod.DefaultMethods)
					allowCredentials = true
					allowHeader(HttpHeaders.AccessControlAllowOrigin)
					allowHeader(HttpHeaders.ContentType)
				}

				install(StatusPages) {
					exception<RequestValidationException> { call, cause ->
						call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
					}
				}

				configureAuth(get(), oAuth2Callback, discordClientId, discordClientSecret)
				configureRequestValidation()
				configureRouting(get())
				configureSerialization()
			}.start(wait = false)
		}, 1)
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesWeb
	}
}

data class UserSession(val state: String, val token: String) : Principal
@Serializable
data class UserInfo(
		val id: String,
		val name: String,
)
