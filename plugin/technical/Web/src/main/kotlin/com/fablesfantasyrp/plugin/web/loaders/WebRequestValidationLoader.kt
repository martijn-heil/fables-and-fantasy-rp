package com.fablesfantasyrp.plugin.web.loaders

import com.fablesfantasyrp.plugin.utils.Loader
import io.ktor.server.plugins.requestvalidation.*

interface WebRequestValidationLoader : Loader<RequestValidationConfig.() -> Unit>
