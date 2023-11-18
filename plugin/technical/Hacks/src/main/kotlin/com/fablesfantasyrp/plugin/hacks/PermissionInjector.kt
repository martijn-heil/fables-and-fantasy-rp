package com.fablesfantasyrp.plugin.hacks

import org.bukkit.entity.Player

interface PermissionInjector {
	fun inject(player: Player, permission: String, value: Boolean?)
}
