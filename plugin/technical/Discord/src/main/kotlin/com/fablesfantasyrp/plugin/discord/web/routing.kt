package com.fablesfantasyrp.plugin.discord.web

import com.fablesfantasyrp.plugin.discord.FablesDiscordBot
import com.fablesfantasyrp.plugin.discord.web.model.transform
import com.fablesfantasyrp.plugin.web.loaders.BaseWebRoutingLoader
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Resource("/calendar")
private class Calendar

class WebRouting(private val bot: FablesDiscordBot) : BaseWebRoutingLoader() {
	override val routes: Route.() -> Unit = {
		get<Calendar> {
			val results = bot.getCalendarEvents().map { it.transform() }
			call.respond(results)
		}
	}
}
