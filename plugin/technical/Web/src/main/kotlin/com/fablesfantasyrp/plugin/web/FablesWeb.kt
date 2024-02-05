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
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec

internal val PLUGIN: FablesWeb
	get() = FablesWeb.instance

class FablesWeb : JavaPlugin(), KoinComponent {
	lateinit var koinModule: Module
	private lateinit var nettyApplicationEngine: NettyApplicationEngine

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

		val port = config.getInt("bind.port")
		val host = config.getString("bind.host")!!
		val jwtPublicKeyString = config.getString("auth.jwt_pubkey")!!

		val jwtPublicKey = getJwtPublicKey(jwtPublicKeyString)

		server.scheduler.scheduleAsyncDelayedTask(this, {
			nettyApplicationEngine = embeddedServer(Netty, port = port, host = host) {
				install(Resources)
				install(ForwardedHeaders)
				install(XForwardedHeaders)

				install(CORS) {
					anyHost()
					allowCredentials = true

					allowMethod(HttpMethod.Options)
					allowMethod(HttpMethod.Put)
					allowMethod(HttpMethod.Patch)
					allowMethod(HttpMethod.Delete)
					HttpMethod.DefaultMethods.forEach { allowMethod(it) }

					allowHeader(HttpHeaders.Authorization)
					allowHeader(HttpHeaders.ContentType)
					allowHeader(HttpHeaders.AccessControlAllowOrigin)
					allowHeader(HttpHeaders.Origin)
					allowHeader(HttpHeaders.Host)
					allowHeader(HttpHeaders.XForwardedFor)
					allowHeader(HttpHeaders.XForwardedProto)
					allowHeader("X-Real-IP")
					this.allowHeaders { true }
				}

				install(StatusPages) {
					exception<RequestValidationException> { call, cause ->
						call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
					}
				}

				configureAuth(logger, jwtPublicKey)
				configureRequestValidation()
				configureRouting()
				configureSerialization()
			}.start(wait = false)
		}, 1)
	}

	private fun getJwtPublicKey(key: String): RSAPublicKey {
		val algorithm = "RSA"
		val keyFactory = KeyFactory.getInstance(algorithm);
		val publicKeyStr = "-----BEGIN PUBLIC KEY-----${key}-----END PUBLIC KEY-----"
		val keySpec = X509EncodedKeySpec(publicKeyStr.toByteArray());
		return keyFactory.generatePublic(keySpec) as RSAPublicKey;
	}

	override fun onDisable() {
		nettyApplicationEngine.stop()
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesWeb
	}
}
