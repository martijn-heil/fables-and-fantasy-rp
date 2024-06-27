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
package com.fablesfantasyrp.plugin.web.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.keycloak.TokenVerifier
import org.keycloak.common.VerificationException
import org.keycloak.representations.AccessToken
import java.security.interfaces.RSAPublicKey
import java.util.logging.Logger

fun Application.configureAuth(logger: Logger, publicKey: RSAPublicKey) {
	install(Authentication) {
		bearer("auth-bearer") {
			authenticate { tokenCredential ->
				val token: AccessToken = try {
					TokenVerifier.create(tokenCredential.token, AccessToken::class.java)
						.publicKey(publicKey)
						.getToken()
				} catch (ex: VerificationException) {
					logger.warning("JWT verification failed: ${ex.message}")
					return@authenticate null
				}

				if (token.realmAccess.roles.contains("fables-web-admin")) {
					UserIdPrincipal(token.preferredUsername)
				} else null
        	}
		}
	}
}
