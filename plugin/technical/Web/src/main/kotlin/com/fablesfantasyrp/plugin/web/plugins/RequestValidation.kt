package com.fablesfantasyrp.plugin.web.plugins

import com.fablesfantasyrp.plugin.web.loaders.WebRequestValidationLoader
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import org.koin.core.context.GlobalContext

fun Application.configureRequestValidation() {
	install(RequestValidation) {
		GlobalContext.get().getAll<WebRequestValidationLoader>().forEach { it.load()(this) }
	}
}
