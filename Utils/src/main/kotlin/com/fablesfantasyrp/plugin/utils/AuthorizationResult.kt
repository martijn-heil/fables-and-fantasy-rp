package com.fablesfantasyrp.plugin.utils

data class AuthorizationResult(val result: Boolean, val message: String? = null) {
	fun orElse(x: (String) -> Unit) {
		if (result) return
		x(message ?: "Permission denied")
	}
}