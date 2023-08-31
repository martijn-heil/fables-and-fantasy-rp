package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.permission.Permission
import org.bukkit.Server
import org.bukkit.entity.Player

class PermissionInjectorImpl(private val server: Server, original: Permission) : DelegatedVaultPermission(original), PermissionInjector {
	private val handlerMap = HashMap<String, (Player) -> Boolean?>()

	override fun playerHas(world: String?, player: String?, permission: String?): Boolean {
		if (player == null) return super.playerHas(world, player as String?, permission)
		val handler = handlerMap[permission] ?: return child.playerHas(world, player, permission)
		val bukkitPlayer = server.getPlayer(player) ?: return child.playerHas(world, player, permission)
		return handler(bukkitPlayer) ?: child.playerHas(world, player, permission)
	}

	override fun inject(permission: String, handler: (Player) -> Boolean?) {
		handlerMap[permission] = handler
	}
}
