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
