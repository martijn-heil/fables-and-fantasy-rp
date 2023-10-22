package com.fablesfantasyrp.plugin.web.plugins

import com.fablesfantasyrp.plugin.web.loaders.WebRoutingLoader
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.context.GlobalContext

fun Application.configureRouting() {
	routing {
		options("/{...}") {  }

		authenticate("auth-bearer") {
			get("/me") {
				call.respond("{\"username\":\"admin\"}")
			}

			GlobalContext.get().getAll<WebRoutingLoader>().forEach { it.load()(this) }
		}
	}
}
