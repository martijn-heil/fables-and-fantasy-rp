package com.fablesfantasyrp.plugin.web.loaders

import com.fablesfantasyrp.plugin.utils.Loader
import io.ktor.server.routing.*

interface WebRoutingLoader : Loader<Route.() -> Unit>
