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
package com.fablesfantasyrp.plugin.whitelist.command

import com.fablesfantasyrp.plugin.whitelist.Permission
import com.fablesfantasyrp.plugin.whitelist.joinMessage
import com.fablesfantasyrp.plugin.whitelist.quitMessage
import com.fablesfantasyrp.caturix.spigot.common.Sender
import com.fablesfantasyrp.caturix.Command
import com.fablesfantasyrp.caturix.Require
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Commands {
	@Command(aliases = ["fjoin", "flogin", "fakejoin", "fakelogin"], desc = "Send a fake login message")
	@Require(Permission.SilentJoinQuit)
	fun fakejoin(@Sender sender: Player) {
		val message = joinMessage(sender, isSilent = false) ?: return
		if (message.recipients != null) {
			message.recipients.forEach { it.sendMessage(message.message) }
		} else {
			Bukkit.broadcast(message.message)
		}
	}

	@Command(aliases = ["fquit", "flogout", "fleave", "fakequit", "fakelogout", "fakeleave"], desc = "Send a fake quit message")
	@Require(Permission.SilentJoinQuit)
	fun fakequit(@Sender sender: Player) {
		val message = quitMessage(sender, isSilent = false) ?: return
		if (message.recipients != null) {
			message.recipients.forEach { it.sendMessage(message.message) }
		} else {
			Bukkit.broadcast(message.message)
		}
	}
}
