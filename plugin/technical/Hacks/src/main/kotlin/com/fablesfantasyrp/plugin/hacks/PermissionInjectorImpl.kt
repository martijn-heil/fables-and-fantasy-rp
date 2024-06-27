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
package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.Plugin
import java.util.*

class PermissionInjectorImpl(private val registeringPlugin: Plugin, original: Permission) : DelegatedVaultPermission(original), PermissionInjector {
	private val server = registeringPlugin.server

	private val permissionMap = HashMap<UUID, HashMap<String, Boolean>>()
	private val attachments = HashMap<UUID, PermissionAttachment>()

	override fun getName(): String = "PermissionInjectorImpl(${child.name})"

	init {
		server.pluginManager.registerEvents(PermissionInjectorImplListener(), registeringPlugin)
	}

	override fun playerHas(world: String?, player: String?, permission: String?): Boolean {
		if (player == null) return super.playerHas(world, player as String?, permission)
		val bukkitPlayer = server.getPlayer(player) ?: return child.playerHas(world, player, permission)
		val definition = permissionMap[bukkitPlayer.uniqueId] ?: return child.playerHas(world, player, permission)
		return definition[permission] ?: child.playerHas(world, player, permission)
	}

	override fun inject(player: Player, permission: String, value: Boolean?) {
		if (value != null) {
			permissionMap.computeIfAbsent(player.uniqueId) { HashMap() }[permission] = value
			attachments[player.uniqueId] = player.addAttachment(registeringPlugin, permission, value)
		} else {
			permissionMap[player.uniqueId]?.remove(permission)
			attachments[player.uniqueId]?.let { player.removeAttachment(it) }
		}
	}

	inner class PermissionInjectorImplListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			permissionMap.remove(e.player.uniqueId)
			attachments[e.player.uniqueId]?.let { e.player.removeAttachment(it) }
			attachments.remove(e.player.uniqueId)
		}
	}
}
