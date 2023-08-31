package com.fablesfantasyrp.plugin.hacks

import org.bukkit.entity.Player

interface PermissionInjector {
	fun inject(permission: String, handler: (Player) -> Boolean?)
}
