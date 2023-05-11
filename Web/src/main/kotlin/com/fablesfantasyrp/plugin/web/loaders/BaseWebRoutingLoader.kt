package com.fablesfantasyrp.plugin.web.loaders

import io.ktor.server.routing.*

abstract class BaseWebRoutingLoader : WebRoutingLoader {
	override fun load(): Route.() -> Unit = routes

	protected abstract val routes: Route.() -> Unit
}
