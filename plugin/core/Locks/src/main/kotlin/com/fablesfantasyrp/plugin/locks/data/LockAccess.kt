package com.fablesfantasyrp.plugin.locks.data

enum class LockAccess {
	STAFF,
	MODERATOR,
	OWNER,
	USER,
	NONE;

	companion object {
		fun fromRole(role: LockRole) = when (role) {
			LockRole.USER -> USER
			LockRole.MODERATOR -> MODERATOR
		}
	}
}
