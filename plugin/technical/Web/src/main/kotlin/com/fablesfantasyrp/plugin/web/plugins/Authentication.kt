package com.fablesfantasyrp.plugin.web.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuth(password: String) {
	install(Authentication) {
		bearer("auth-bearer") {
			authenticate { tokenCredential ->
				if (tokenCredential.token == password) {
					UserIdPrincipal("admin")
				} else {
					null
				}
        	}
		}
	}
}
