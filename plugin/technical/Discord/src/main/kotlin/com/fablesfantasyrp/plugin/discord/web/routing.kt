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
