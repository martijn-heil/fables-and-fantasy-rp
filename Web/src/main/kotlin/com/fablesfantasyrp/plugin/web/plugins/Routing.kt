package com.fablesfantasyrp.plugin.web.plugins

import com.fablesfantasyrp.plugin.web.PLUGIN
import com.fablesfantasyrp.plugin.web.loaders.WebRoutingLoader
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext

fun Application.configureRouting() {
	routing {
		authenticate("auth-bearer") {
			get("/time") {
				val time = withContext(PLUGIN.minecraftDispatcher) {
					PLUGIN.server.worlds.find { it.name == "world" }!!.time
				}
				call.respondText("Current time in world: $time")
			}

			GlobalContext.get().getAll<WebRoutingLoader>().forEach { it.load()(this) }
		}
	}
}
