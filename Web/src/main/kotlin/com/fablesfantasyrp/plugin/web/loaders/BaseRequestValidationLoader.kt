package com.fablesfantasyrp.plugin.web.loaders

import io.ktor.server.plugins.requestvalidation.*

abstract class BaseRequestValidationLoader : WebRequestValidationLoader {
	override fun load(): RequestValidationConfig.() -> Unit = validation

	protected abstract val validation: RequestValidationConfig.() -> Unit
}
